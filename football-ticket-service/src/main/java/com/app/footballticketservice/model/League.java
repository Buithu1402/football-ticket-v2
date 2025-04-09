package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class League {
    private long leagueId;
    private String name;
    private String logo;
    private String status;

    public League(ResultSet rs) throws SQLException {
        this(
                rs.getLong("league_id"),
                rs.getString("name"),
                rs.getString("logo"),
                rs.getString("status")
        );
    }

    public League(Long leagueId, String name, String logo) {
        this(
                leagueId,
                name,
                logo,
                "ACTIVE"
        );
    }
}
