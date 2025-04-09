package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.dto.LeagueDTO;
import com.app.footballticketservice.exception.AppException;
import com.app.footballticketservice.model.League;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeagueService {
    private final NamedParameterJdbcTemplate writeDB;

    public LeagueService(@WriteDB NamedParameterJdbcTemplate writeDB) {
        this.writeDB = writeDB;
    }

    @Transactional
    public void save(League league) {
        if (existsName(league.getName(), league.getLeagueId())) {
            throw new AppException("400", "League name already exists");
        }
        if (league.getLeagueId() > 0) {
            save(league.getLogo(), league.getName(), league.getLeagueId());
        } else {
            save(league.getLogo(), league.getName());
        }
    }

    @Transactional
    public void save(String logo, String name, long leagueId) {
        var sql = """
                update leagues
                set logo = COALESCE(:logo, logo),
                    name = COALESCE(:name, name)
                where league_id = :league_id;
                """;
        var param = new MapSqlParameterSource()
                .addValue("logo", logo)
                .addValue("name", name)
                .addValue("league_id", leagueId);
        writeDB.update(sql, param);
    }

    @Transactional
    public void save(String logo, String name) {
        var sql = "insert into leagues (logo, name) values (:logo, :name);";
        var param = new MapSqlParameterSource()
                .addValue("logo", logo)
                .addValue("name", name);
        writeDB.update(sql, param);
    }

    public List<League> findAll(Integer offset, Integer size, String keyword) {
        String sql = """
                select league_id, logo, name, status
                from leagues
                where true
                    and name like :keyword
                    and status = 'active'
                order by league_id
                limit :size offset :offset;
                """;
        var param = new MapSqlParameterSource()
                .addValue("keyword", "%" + keyword + "%")
                .addValue("size", size)
                .addValue("offset", offset);
        return writeDB.query(sql, param, (rs, i) -> new League(rs));
    }

    public List<LeagueDTO> findAll(Integer offset, Integer size) {
        var sql = """
                SELECT l.league_id,
                       l.logo,
                       l.name,
                       COUNT(DISTINCT m.match_id)                                                AS match_count,
                       COALESCE(MIN(t.price), 0)                                                 AS min_price,
                       COUNT(DISTINCT CASE WHEN m.match_date < DATE(NOW()) THEN m.match_id END)  AS match_today,
                       COUNT(DISTINCT CASE WHEN m.match_date >= DATE(NOW()) THEN m.match_id END) AS match_tomorrow,
                       COUNT(DISTINCT CASE WHEN m.status = 'live' THEN m.match_id END)           AS match_live,
                       COUNT(DISTINCT CASE WHEN m.status = 'pending' THEN m.match_id END)        AS match_pending
                FROM leagues l
                         LEFT JOIN matches m ON l.league_id = m.league_id AND m.status <> 'completed'
                         LEFT JOIN tickets t ON m.match_id = t.match_id
                WHERE l.status = 'active'
                GROUP BY l.league_id
                ORDER BY l.league_id
                limit :size offset :offset
                 """;
        var param = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("offset", offset);
        return writeDB.query(sql, param, BeanPropertyRowMapper.newInstance(LeagueDTO.class));
    }

    public List<League> findAll(Integer n) {
        String sql = """
                select league_id, logo, name, status
                from leagues
                where status = 'active'
                order by league_id desc
                limit :n;
                """;
        var param = new MapSqlParameterSource().addValue("n", n);
        return writeDB.query(sql, param, (rs, i) -> new League(rs));
    }

    public boolean existsName(String name, long leagueId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM leagues WHERE name = :name AND (:leagueId = 0 OR league_id <> :leagueId));";
        var param = new MapSqlParameterSource().addValue("name", name).addValue("leagueId", leagueId);
        return writeDB.queryForObject(sql, param, Boolean.class);
    }

    public long count(String keyword) {
        String sql = "select count(1) from leagues where name like :keyword and status = 'active';";
        var param = new MapSqlParameterSource().addValue("keyword", "%" + keyword + "%");
        return writeDB.queryForObject(sql, param, Long.class);
    }

    @Transactional
    public void delete(long leagueId) {
        String sql = """
                update leagues
                set status = 'inactive',
                    name = concat(name, '_deleted_', now())
                where league_id = :league_id;
                """;
        var param = new MapSqlParameterSource().addValue("league_id", leagueId);
        writeDB.update(sql, param);
    }
}
