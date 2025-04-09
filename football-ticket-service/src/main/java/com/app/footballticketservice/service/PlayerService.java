package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.ReadDB;
import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.model.Player;
import com.app.footballticketservice.utils.PagingUtil;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerService {
    private final NamedParameterJdbcTemplate writeDB;
    private final NamedParameterJdbcTemplate readDB;

    public PlayerService(@WriteDB NamedParameterJdbcTemplate writeDB, @ReadDB NamedParameterJdbcTemplate readDB) {
        this.writeDB = writeDB;
        this.readDB = readDB;
    }

    @Transactional
    public void save(Player player) {
        String sql;
        if (player.getPlayerId() > 0) {
            sql = """
                    update players
                    set name = :name,
                        position = :position,
                        team_id = :team_id,
                        no = :no,
                        avatar = :avatar,
                        nation = :nation,
                        match = :match,
                        goal = :goal
                    where player_id = :player_id;
                    """;
        } else {
            sql = """
                    insert into players(team_id, name, position, no, avatar, nation, `match`, goal)
                    VALUES (:team_id, :name, :position, :no, :avatar, :nation, :match, :goal);
                    """;
        }
        writeDB.update(sql, player.toMap());
    }

    public List<Player> findAll(Integer page, Integer size, String keyword) {
        String sql = """
                select *
                from players
                where name like :keyword
                order by player_id
                limit :size offset :offset;
                """;
        var param = new MapSqlParameterSource()
                .addValue("keyword", "%" + keyword + "%")
                .addValue("size", size)
                .addValue("offset", PagingUtil.calculateOffset(page, size));
        return readDB.query(sql, param, (rs, i) -> new Player(rs));
    }

    @Transactional
    public void delete(long playerId) {
        String sql = "update players set status = 'inactive' where player_id = :player_id;";
        var param = new MapSqlParameterSource().addValue("player_id", playerId);
        writeDB.update(sql, param);
    }
}
