package com.app.footballticketservice.rest;

import com.app.footballticketservice.dto.BookSendEmailDTO;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.request.payload.MatchListPayload;
import com.app.footballticketservice.request.payload.RegisterPayload;
import com.app.footballticketservice.service.*;
import com.app.footballticketservice.utils.PagingUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("public")
@RequiredArgsConstructor
public class PublicController {
    private final MatchService matchService;
    private final TeamService teamService;
    private final LeagueService leagueService;
    private final AuthService authService;
    private final EmailService emailService;
    private final NamedParameterJdbcTemplate writeDb;

    @GetMapping("league")
    public Object league(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        var offset = PagingUtil.calculateOffset(page, size);
        var data = leagueService.findAll(offset, size);
        var numberOfRecords = leagueService.count("");
        return ResponseContainer.success(new PagingContainer<>(page, size, numberOfRecords, data));
    }

    @GetMapping("testt")
    public Object testt() throws MessagingException, IOException {
        var sqlSendEmail = """
                select GROUP_CONCAT(s.seat_number) as seats,
                         home.name                   as home_team,
                         away.name                   as away_team,
                         home.logo                   as home_logo,
                         away.logo                   as away_logo,
                         m.match_date,
                         m.match_time,
                         st.stadium_name             as stadium
                  from bookings b
                           inner join matches m on b.match_id = m.match_id
                           inner join stadium st on m.stadium_id = st.stadium_id
                           inner join teams home on m.home_id = home.team_id
                           inner join teams away on m.away_id = away.team_id
                           inner join booking_seat bs on b.booking_id = bs.booking_id
                           inner join seat s on bs.seat_id = s.seat_id
                  where b.txn_ref = 1743082696366
                  group by home.name, away.name, home.logo, away.logo, m.match_date, m.match_time, st.stadium_name
                """;
        var result = writeDb.queryForObject(
                sqlSendEmail,
                Map.of(),
                BeanPropertyRowMapper.newInstance(BookSendEmailDTO.class)
        );
        emailService.sendBooking(result, User.builder().email("").build());
        return ResponseContainer.success("Test");
    }

    @GetMapping("initAdmin")
    public Object initAdmin(@RequestParam("password") String password, @RequestParam("email") String email) {
        authService.register(new RegisterPayload(email, password, "admin", "admin"));
        return ResponseContainer.success("Admin user created successfully");
    }

    @GetMapping("team/{teamId}")
    public Object getTeam(@PathVariable long teamId) {
        return ResponseContainer.success(teamService.detail(teamId));
    }

    @GetMapping("match/{matchId}")
    public Object get(@PathVariable long matchId) {
        return ResponseContainer.success(matchService.detail(matchId));
    }

    @GetMapping("match/top")
    public Object matchTopN(@RequestParam(defaultValue = "10") int n) {
        return ResponseContainer.success(matchService.findTopN(n));
    }

    @GetMapping("team/top")
    public Object teamTopN(@RequestParam(defaultValue = "6") int n) {
        return ResponseContainer.success(teamService.rankTopN(n));
    }

    @GetMapping("match")
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "leagueId", defaultValue = "-1") Long leagueId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword

    ) {
        return ResponseContainer.success(matchService.findAll(new MatchListPayload(
                page,
                size,
                keyword,
                null,
                leagueId,
                List.of("pending", "live")
        )));
    }

    @GetMapping("team")
    public Object findAll(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword
    ) {
        var offset = PagingUtil.calculateOffset(page, size);
        var data = teamService.findAll(offset, size, keyword);
        var total = teamService.count(keyword);
        return ResponseContainer.success(new PagingContainer<>(page, size, total, data));
    }

    @GetMapping("league/top")
    public Object findAllLeague(
            @RequestParam(value = "n", defaultValue = "6") Integer n
    ) {
        return ResponseContainer.success(leagueService.findAll(n));
    }
}
