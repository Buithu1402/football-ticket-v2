package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.dto.LeagueRevenueDTO;
import com.app.footballticketservice.dto.LeagueStatisticDTO;
import com.app.footballticketservice.dto.RevenueDTO;
import com.app.footballticketservice.dto.StatisticTicketDTO;
import com.app.footballticketservice.model.ChartData;
import com.app.footballticketservice.model.Datasets;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.utils.PagingUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class StatisticService {
    private final NamedParameterJdbcTemplate writeDb;

    public StatisticService(@WriteDB NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    public ChartData revenueByLeague(String from, String to) {
        var sql = """
                SELECT l.name     as league_name,
                       SUM(total) AS total_revenue
                FROM bookings b
                         INNER JOIN matches m on b.match_id = m.match_id
                         INNER JOIN leagues l on l.league_id = m.league_id
                WHERE b.status = 'completed'
                  AND b.created_at >= :start_date
                  AND b.created_at <= :end_date
                GROUP BY l.league_id, l.name
                ORDER BY l.name DESC
                """;
        var params = Map.of("start_date", from, "end_date", to);
        var rerult = writeDb.query(sql, params, BeanPropertyRowMapper.newInstance(LeagueRevenueDTO.class));
        var labels = rerult.stream().map(LeagueRevenueDTO::getLeagueName).toList();
        var series = rerult.stream()
                           .map(e -> {
                               var data = labels.stream()
                                                .map(label -> e.getLeagueName().equals(label)
                                                        ? e.getTotalRevenue()
                                                        : BigDecimal.ZERO)
                                                .toList();
                               return new Datasets(data, e.getLeagueName());
                           })
                           .toList();
        return new ChartData(labels, series);
    }

    public PagingContainer<StatisticTicketDTO> getTicketStatistic(int page, int size) {
        var sql = """
                select m.match_id,
                       concat(home.name, ' vs ', away.name)             as match_name,
                       home.logo                                        as home_logo,
                       away.logo                                        as away_logo,
                       count(ts.seat_id)                                as total_ticket,
                       count(case when ts.is_booked = 'yes' then 1 end) as total_ticket_sold,
                       count(case when ts.is_booked = 'no' then 1 end)  as total_ticket_available
                from matches m
                         inner join teams home
                                    on m.away_id = home.team_id
                         inner join teams away on m.home_id = away.team_id
                         inner join stadium s on m.stadium_id = s.stadium_id
                         left join bookings b on m.match_id = b.match_id
                         left join ticket_seat ts on m.match_id = ts.match_id
                group by m.match_id, home.name, away.name, s.stadium_name, m.status
                order by m.match_id desc, m.status + 1
                limit :size offset :offset;
                """;
        var offset = PagingUtil.calculateOffset(page, size);
        var params = Map.of("size", size, "offset", offset);
        var result = writeDb.query(sql, params, BeanPropertyRowMapper.newInstance(StatisticTicketDTO.class));
        var sqlCount = """
                select count(1) from matches m
                """;
        var total = writeDb.queryForObject(sqlCount, Map.of(), Integer.class);
        return new PagingContainer<>(page, size, total, result);
    }

    public ChartData getRevenue(String from, String to, String type) {
        var sql = """
                 SELECT CASE
                            WHEN :type = 'D' THEN DATE(created_at)
                            WHEN :type = 'M' THEN DATE_FORMAT(created_at, '%Y-%m')
                            END    AS period,
                        SUM(total) AS total_revenue,
                        COUNT(1)   AS total_bookings
                 FROM bookings
                 WHERE status = 'completed'
                   AND created_at >= :start_date
                   AND created_at <= :end_date
                 GROUP BY period
                 ORDER BY period DESC
                """;
        var params = Map.of("start_date", from, "end_date", to, "type", type);
        var result = writeDb.query(sql, params, BeanPropertyRowMapper.newInstance(RevenueDTO.class));
        var labels = result.stream().map(RevenueDTO::getPeriod).toList();
        var bookingData = result.stream()
                                .map(e -> {
                                    var data = labels.stream()
                                                     .map(label -> e.getPeriod().equals(label)
                                                             ? e.getTotalRevenue()
                                                             : BigDecimal.ZERO)
                                                     .toList();
                                    return new Datasets(data, e.getPeriod());
                                })
                                .toList(); // Convert to BigDecimal
        return new ChartData(labels, bookingData);
    }

    public List<LeagueStatisticDTO> getLeagueStatistic() {
        var sql = """
                select l.name
                     , l.logo                                as league_logo
                     , count(m.match_id)                     as match_count
                     , sum(IF(m.status = 'completed', 1, 0)) as completed_match_count
                     , sum(IF(m.status = 'pending', 1, 0))   as pending_match_count
                     , sum(IF(m.status = 'live', 1, 0))      as live_match_count
                from leagues l
                         inner join matches m on l.league_id = m.league_id
                group by l.league_id;
                """;
        return writeDb.query(sql, Map.of(), BeanPropertyRowMapper.newInstance(LeagueStatisticDTO.class));
    }
}
