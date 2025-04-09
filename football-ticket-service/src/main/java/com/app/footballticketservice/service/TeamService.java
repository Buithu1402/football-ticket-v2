package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.dto.LastResultDTO;
import com.app.footballticketservice.dto.TeamDetailDTO;
import com.app.footballticketservice.model.Team;
import com.app.footballticketservice.request.response.TeamDetailResponse;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class TeamService {
    private final NamedParameterJdbcTemplate writeDb;

    public TeamService(@WriteDB NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    public Object detail(long teamId) {
        var sqlLastResult = """
                 select concat(m.match_date, ' ', m.match_time) as match_datetime,
                        home.name                               as home_team,
                        away.name                               as away_team,
                        m.home_goal                             as home_score,
                        m.away_goal                             as away_score,
                        home.logo                               as home_logo,
                        away.logo                               as away_logo,
                        m.status,
                        l.name                                  as league_name
                 from matches m
                          inner join leagues l on m.league_id = l.league_id
                          inner join teams home on m.home_id = home.team_id
                          inner join teams away on m.away_id = away.team_id
                 where true
                   and (m.home_id = :teamId or m.away_id = :teamId)
                   and m.status = 'completed'
                 order by match_datetime desc
                 limit 5;
                """;
        var param = new MapSqlParameterSource().addValue("teamId", teamId);
        var last5Match = writeDb.query(
                sqlLastResult,
                param,
                BeanPropertyRowMapper.newInstance(LastResultDTO.class)
        );

        var sqlTeamDetail = """
                select name,
                       logo,
                       address,
                       goals,
                       win,
                       draw,
                       losses,
                       match_played,
                       established_year
                from teams
                where team_id = :teamId;
                """;
        var teamDetail = writeDb.query(sqlTeamDetail, param, BeanPropertyRowMapper.newInstance(TeamDetailDTO.class))
                                .stream().findFirst().orElse(null);
        var lastResult = last5Match.stream().max(Comparator.comparing(LastResultDTO::getMatchDatetime));
        return new TeamDetailResponse()
                .withLastResult(lastResult.orElse(null))
                .withTeamDetail(teamDetail)
                .withLast5Match(last5Match);
    }

    @Transactional
    public void save(Team team) {
        String sql;
        MapSqlParameterSource param;
        if (team.getTeamId() < 1) {
            sql = """
                    insert into teams(name, description, address, logo, established_year, stadium_map, stadium_name)
                    VALUES (:name, :description, :address, :logo, :established_year, :stadium_map, :stadium_name);
                    """;
            param = new MapSqlParameterSource()
                    .addValue("name", team.getName())
                    .addValue("description", team.getDescription())
                    .addValue("address", team.getAddress())
                    .addValue("logo", team.getLogo())
                    .addValue("established_year", team.getEstablishedYear())
                    .addValue("stadium_map", team.getStadiumMap())
                    .addValue("stadium_name", team.getStadiumName());
        } else {
            sql = """
                    update teams
                    set name = IFNULL(:name, name),
                        description = IFNULL(:description, description),
                        address = IFNULL(:address, address),
                        logo = IFNULL(:logo, logo),
                        established_year = IFNULL(:established_year, established_year),
                        stadium_map = IFNULL(:stadium_map, stadium_map),
                        stadium_name = IFNULL(:stadium_name, stadium_name),
                        win = IFNULL(:win, win),
                        goals = IFNULL(:goals, goals),
                        draw = IFNULL(:draw, draw),
                        losses = IFNULL(:losses, losses),
                        match_played = IFNULL(:match_played, match_played)
                    where team_id = :team_id;
                    """;
            param = new MapSqlParameterSource()
                    .addValue("name", team.getName())
                    .addValue("description", team.getDescription())
                    .addValue("address", team.getAddress())
                    .addValue("logo", team.getLogo())
                    .addValue("established_year", team.getEstablishedYear())
                    .addValue("stadium_map", team.getStadiumMap())
                    .addValue("stadium_name", team.getStadiumName())
                    .addValue("win", team.getWin())
                    .addValue("goals", team.getGoals())
                    .addValue("draw", team.getDraw())
                    .addValue("losses", team.getLosses())
                    .addValue("match_played", team.getMatchPlayed())
                    .addValue("team_id", team.getTeamId());
        }
        writeDb.update(sql, param);
    }

    public List<Team> findAll(int offset, int size, String keyword) {
        var sql = """
                select *
                from teams
                where true
                  and status = 'active'
                  and (name like :keyword or description like :keyword)
                limit :pLimit offset :pOffset;
                """;
        var param = new MapSqlParameterSource()
                .addValue("keyword", "%" + keyword + "%")
                .addValue("pLimit", size)
                .addValue("pOffset", offset);
        return writeDb.query(sql, param, (rs, i) -> new Team(rs));
    }

    public List<Team> rankTopN(int n) {
        var sql = """
                SELECT *
                FROM teams
                WHERE true
                AND status = 'ACTIVE'
                ORDER BY win DESC, goals DESC
                LIMIT :n;
                """;
        var param = new MapSqlParameterSource().addValue("n", n);
        return writeDb.query(sql, param, (rs, i) -> new Team(rs));
    }

    public long count(String keyword) {
        var sql = "select count(1) from teams where (name like :keyword or description like :keyword) and status = 'active';";
        var param = new MapSqlParameterSource().addValue("keyword", "%" + keyword + "%");
        return writeDb.queryForObject(sql, param, Long.class);
    }

    @Transactional
    public void delete(long teamId) {
        var sql = "update teams set status = 'inactive' where team_id = :team_id;";
        var param = new MapSqlParameterSource().addValue("team_id", teamId);
        writeDb.update(sql, param);
    }
}
