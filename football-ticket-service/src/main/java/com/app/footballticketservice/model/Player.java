package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Player {
    long playerId;
    long teamId;
    String name;
    String position;
    int no;
    String avatar;
    String nation;
    int matchPlayed;
    int goals;
    String status;

    public Player(ResultSet rs) throws SQLException {
        this(
                rs.getLong("player_id"),
                rs.getLong("team_id"),
                rs.getString("name"),
                rs.getString("position"),
                rs.getInt("no"),
                rs.getString("avatar"),
                rs.getString("nation"),
                rs.getInt("match"),
                rs.getInt("goal"),
                rs.getString("status")
        );
    }

    public MapSqlParameterSource toMap() {
        var rs = new MapSqlParameterSource()
                .addValue("team_id", teamId)
                .addValue("name", name)
                .addValue("position", position)
                .addValue("no", no)
                .addValue("avatar", avatar)
                .addValue("nation", nation)
                .addValue("match", matchPlayed)
                .addValue("goal", goals);
        return playerId > 0 ? rs.addValue("player_id", playerId) : rs;
    }
}
