package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.dto.*;
import com.app.footballticketservice.exception.AppException;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.Seat;
import com.app.footballticketservice.request.payload.MatchListPayload;
import com.app.footballticketservice.request.payload.MatchTicketPayload;
import com.app.footballticketservice.request.payload.UpsertMatchPayload;
import com.app.footballticketservice.request.response.MatchDetailResponse;
import com.app.footballticketservice.request.response.TicketResponse;
import com.app.footballticketservice.request.response.TicketSeatResponse;
import com.app.footballticketservice.utils.PagingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final NamedParameterJdbcTemplate writeDB;

    public MatchService(@WriteDB NamedParameterJdbcTemplate writeDB) {
        this.writeDB = writeDB;
    }


    public List<Seat> getSeats(long matchId) {
        var sql = """
                 select s.*
                 from matches m
                          inner join seat s on s.stadium_id = m.stadium_id
                 where m.match_id = :match_id;
                """;
        return writeDB.query(
                sql,
                new MapSqlParameterSource().addValue("match_id", matchId),
                BeanPropertyRowMapper.newInstance(Seat.class)
        );
    }

    public MatchDetailResponse detail(long matchId) {
        var sql = """
                select m.match_id
                     , home.name      as home_team
                     , away.name      as away_team
                     , home.logo      as home_logo
                     , away.logo      as away_logo
                     , m.home_goal    as home_goal
                     , m.away_goal    as away_goal
                     , m.match_date
                     , m.match_time
                     , m.status       as match_status
                     , l.name         as league_name
                     , s.stadium_name as stadium
                     , s.image        as stadium_image
                from matches m
                         inner join stadium s on m.stadium_id = s.stadium_id
                         inner join teams home on home.team_id = m.home_id
                         inner join teams away on away.team_id = m.away_id
                         inner join leagues l on m.league_id = l.league_id
                where m.match_id = :matchId;
                """;
        var param = new MapSqlParameterSource().addValue("matchId", matchId);
        var result = writeDB.query(sql, param, BeanPropertyRowMapper.newInstance(MatchDetailDTO.class))
                            .stream()
                            .findFirst()
                            .orElse(new MatchDetailDTO());
        var types = getListSeatTicket(matchId);
        return new MatchDetailResponse(result, types);
    }

    public List<TicketSeatResponse> getListSeatTicket(long matchId) {
        var sql = """
                select t.type_id
                     , t.name
                     , ti.price
                     , ti.status
                     , ti.description
                     , s.seat_id
                     , s.seat_number
                from type t
                         left join ticket_seat ts on ts.type_id = t.type_id and ts.match_id = :matchId
                         left join seat s on ts.seat_id = s.seat_id
                         left join tickets ti on ti.match_id = :matchId and ti.type_id = t.type_id
                where ts.is_booked = 'no';
                """;
        var param = new MapSqlParameterSource().addValue("matchId", matchId);
        return writeDB.query(sql, param, BeanPropertyRowMapper.newInstance(TypeDTO.class))
                      .stream()
                      .collect(Collectors.groupingBy(TypeDTO::getTypeId))
                      .entrySet()
                      .stream()
                      .map(TicketSeatResponse::new)
                      .toList();
    }

    @Transactional
    public void saveScore(ScoreMatchDTO payload) {
        writeDB.update(
                """
                        update matches
                            set home_goal = :homeScore,
                                away_goal = :awayScore,
                                status = :status
                        where match_id = :matchId;
                        """,
                new MapSqlParameterSource()
                        .addValue("matchId", payload.getMatchId())
                        .addValue("homeScore", payload.getHomeScore())
                        .addValue("awayScore", payload.getAwayScore())
                        .addValue("status", payload.getStatus())
        );
    }

    public ScoreMatchDTO getScore(long matchId) {
        var sql = """
                select match_id
                    , home_goal as home_score
                    , away_goal as away_score
                    , status
                from matches
                where match_id = :matchId;
                """;
        return writeDB.queryForObject(
                sql,
                new MapSqlParameterSource().addValue("matchId", matchId),
                BeanPropertyRowMapper.newInstance(ScoreMatchDTO.class)
        );
    }

    @Transactional
    public List<Long> upsertTicket(List<MatchTicketPayload> payloads) {
        var params = payloads.stream()
                             .filter(this::filterMatchTicketPayload)
                             .map(MatchTicketPayload::toMap)
                             .toArray(MapSqlParameterSource[]::new);
        var sql = """
                INSERT INTO tickets(match_id, type_id, price, description)
                VALUES (:match_id, :type_id, :price, :description)
                ON DUPLICATE KEY UPDATE price       = :price,
                                        description = :description;
                """;
        writeDB.batchUpdate(sql, params);
        sql = """
                INSERT INTO ticket_seat(match_id, seat_id, type_id)
                VALUES (:match_id, :seat_id, :type_id)
                ON DUPLICATE KEY UPDATE seat_id = :seat_id, type_id = :type_id
                """;
        writeDB.batchUpdate(
                sql,
                payloads.stream()
                        .filter(payload -> payload.seatIds() != null)
                        .flatMap(payload -> payload.seatIds().stream().map(seatId -> new MapSqlParameterSource()
                                .addValue("match_id", payload.matchId())
                                .addValue("type_id", payload.typeId())
                                .addValue("seat_id", seatId)))
                        .toArray(MapSqlParameterSource[]::new)
        );
        return payloads.stream().filter(this::exceptMatchTicketPayload).map(MatchTicketPayload::typeId).toList();
    }

    public List<TicketResponse> getTickets(long matchId) {
        var sql = """
                  select t1.type_id,
                         t1.name,
                         t2.match_id,
                         t2.price,
                         t2.description,
                         t4.seat_number,
                         t4.seat_id
                  from type t1
                           left join ticket_seat t3 on t3.type_id = t1.type_id and t3.match_id = :matchId
                           left join seat t4 on t3.seat_id = t4.seat_id
                           left join tickets t2 on t1.type_id = t2.type_id and t2.match_id = :matchId
                  where true
                     or t2.match_id is null;
                """;
        return writeDB
                .query(
                        sql,
                        new MapSqlParameterSource().addValue("matchId", matchId),
                        BeanPropertyRowMapper.newInstance(TicketDTO.class)
                ).stream()
                .collect(Collectors.groupingBy(TicketDTO::getTypeId))
                .entrySet()
                .stream()
                .map(TicketResponse::new)
                .toList();
    }

    public Object findById(long matchId) {
        var sql = """
                select m.match_id,
                       m.match_date,
                       m.match_time,
                       m.home_goal,
                       m.away_goal,
                       m.status,
                       l.name            as league_name,
                       home.name         as home_team,
                       away.name         as away_team,
                       home.logo         as home_logo,
                       away.logo         as away_logo,
                       home.stadium_name as home_stadium
                from matches m
                         inner join teams home on m.home_id = home.team_id
                         inner join teams away on m.away_id = away.team_id
                         inner join leagues l on m.league_id = l.league_id
                where match_id = :matchId;
                """;
        return null;
    }

    @Transactional
    public void save(UpsertMatchPayload match) {
        var paramCheck = new MapSqlParameterSource()
                .addValue("stadiumId", match.getStadiumId())
                .addValue("matchDate", match.getMatchDate())
                .addValue("matchTime", match.getMatchTime());
        var check = writeDB.queryForObject(
                "CALL up_CheckStadiumFree(:stadiumId, :matchDate, :matchTime)",
                paramCheck,
                Boolean.class
        );

        if (check) {
            throw new AppException("400", "Stadium is not free");
        }

        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("league_id", match.getLeagueId())
                .addValue("home_team_id", match.getHomeTeamId())
                .addValue("away_team_id", match.getAwayTeamId())
                .addValue("match_date", match.getMatchDate())
                .addValue("stadium_id", match.getStadiumId())
                .addValue("match_time", match.getMatchTime());
        if (match.getMatchId() > 0) {
            sql = """
                    update matches
                        set league_id  = IFNULL(:league_id, league_id),
                        home_id    = IFNULL(:home_team_id, home_id),
                        away_id    = IFNULL(:away_team_id, away_id),
                        home_goal  = IFNULL(:home_goal, home_goal),
                        away_goal  = IFNULL(:away_goal, away_goal),
                        match_date = IFNULL(:match_date, match_date),
                        match_time = IFNULL(:match_time, match_time),
                        stadium_id = IFNULL(:stadium_id, stadium_id),
                        status     = IFNULL(:status, status)
                    where match_id = :match_id;
                    """;
            params.addValue("match_id", match.getMatchId())
                  .addValue("home_goal", match.getHomeGoals())
                  .addValue("away_goal", match.getAwayGoals())
                  .addValue("status", match.getStatus());
            writeDB.update(sql, params);
        } else {
            var keyHolder = new GeneratedKeyHolder();
            sql = """
                    insert into matches(league_id, home_id, away_id, home_goal, away_goal, match_date, match_time,stadium_id)
                    VALUES (:league_id, :home_team_id, :away_team_id, 0, 0, :match_date, :match_time, :stadium_id);
                    """;
            writeDB.update(sql, params, keyHolder);
            var lastId = keyHolder.getKey().longValue();
            var types = writeDB.query(
                    "select type_id from type;",
                    new HashMap<>(),
                    (rs, rowNum) -> rs.getLong("type_id")
            );
            var ticketParams = types.stream()
                                    .map(typeId -> new MapSqlParameterSource()
                                            .addValue("matchId", lastId)
                                            .addValue("typeId", typeId)
                                            .addValue("description", "")
                                    )
                                    .toArray(MapSqlParameterSource[]::new);
            writeDB.batchUpdate(
                    "INSERT INTO tickets(match_id, type_id, price, description) VALUES (:matchId, :typeId, NULL, :description);",
                    ticketParams
            );

            var seatsSql = "SELECT * FROM seat WHERE stadium_id = :stadiumId;";
            var seatParams = new MapSqlParameterSource()
                    .addValue("stadiumId", match.getStadiumId());
            var seats = writeDB.query(
                    seatsSql,
                    seatParams,
                    BeanPropertyRowMapper.newInstance(Seat.class)
            );
            var ticketSeatParams = seats.stream()
                                        .map(seat -> new MapSqlParameterSource()
                                                .addValue("matchId", lastId)
                                                .addValue("seatId", seat.getSeatId())
                                                .addValue("typeId", seat.getTypeId())
                                        )
                                        .toArray(MapSqlParameterSource[]::new);
            writeDB.batchUpdate(
                    "INSERT INTO ticket_seat(match_id, seat_id, type_id) VALUES (:matchId, :seatId, :typeId);",
                    ticketSeatParams
            );
        }
    }

    public PagingContainer<MatchDTO> findAll(MatchListPayload payload) {
        var sql = """
                select m.match_id,
                       m.match_date,
                       m.match_time,
                       IFNULL(m.home_goal, 0) as home_goal,
                       IFNULL(m.away_goal, 0) as away_goal,
                       m.status,
                       home.name              as home_team_name,
                       away.name              as away_team_name,
                       home.logo              as home_logo,
                       away.logo              as away_logo,
                       s.stadium_name         as stadium_name,
                       l.name                 as league_name
                from matches m
                         inner join stadium s on m.stadium_id = s.stadium_id
                         inner join teams home on home.team_id = m.home_id and home.status = 'active'
                         inner join teams away on away.team_id = m.away_id and away.status = 'active'
                         inner join leagues l on m.league_id = l.league_id and (:leagueId = -1 OR :leagueId IS NULL OR l.league_id = :leagueId)
                where true
                  and ('ALL' IN (:status) or m.status in (:status))
                  and (home.name like :keyword or away.name like :keyword)
                  and (:dateFrom is null or m.match_date = :dateFrom)
                order by m.status+0, m.match_date, m.match_time desc
                limit :limit offset :offset
                """;
        var param = new MapSqlParameterSource()
                .addValue("status", payload.status())
                .addValue("keyword", "%" + StringUtils.defaultIfEmpty(payload.key(), "") + "%")
                .addValue("dateFrom", payload.date())
                .addValue("leagueId", payload.leagueId())
                .addValue("limit", payload.size())
                .addValue("offset", PagingUtil.calculateOffset(payload.page(), payload.size()));
        var result = writeDB.query(sql, param, BeanPropertyRowMapper.newInstance(MatchDTO.class));
        var count = writeDB.queryForObject(
                """
                        select count(1)
                        from matches m
                        inner join teams home on home.team_id = m.home_id and home.status = 'active'
                         inner join teams away on away.team_id = m.away_id and away.status = 'active'
                         inner join leagues l on m.league_id = l.league_id and (:leagueId = -1 OR :leagueId IS NULL OR l.league_id = :leagueId)
                        where true
                         and ('ALL' IN (:status ) or m.status in (:status))
                         and (home.name like :keyword or away.name like :keyword)
                         and (:dateFrom is null or m.match_date = :dateFrom);
                        """,
                param,
                Long.class
        );
        return new PagingContainer<>(payload.page(), payload.size(), count, result);
    }

    public List<MatchDTO> findTopN(int n) {
        return findAll(new MatchListPayload(1, n, null, null, -1L, List.of("PENDING", "LIVE"))).getContents();
    }

    private boolean filterMatchTicketPayload(MatchTicketPayload payload) {
        return payload.typeId() != null
               && payload.matchId() > 0
               && payload.typeId() > 0
               && payload.price() != null
               && payload.price().signum() >= 0;
    }

    private boolean exceptMatchTicketPayload(MatchTicketPayload payload) {
        return !this.filterMatchTicketPayload(payload);
    }
}
