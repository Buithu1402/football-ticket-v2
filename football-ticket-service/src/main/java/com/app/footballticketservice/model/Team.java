package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Team {
    long teamId;
    String name;
    String description;
    String address;
    String logo;
    int establishedYear;
    String stadiumMap;
    String stadiumName;
    int matchPlayed;
    int win;
    int losses;
    int draw;
    int goals;
    String status;

    public Team(ResultSet rs) throws SQLException {
        this(
                rs.getLong("team_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("address"),
                rs.getString("logo"),
                rs.getInt("established_year"),
                rs.getString("stadium_map"),
                rs.getString("stadium_name"),
                rs.getInt("match_played"),
                rs.getInt("win"),
                rs.getInt("losses"),
                rs.getInt("draw"),
                rs.getInt("goals"),
                rs.getString("status")
        );
    }
}
