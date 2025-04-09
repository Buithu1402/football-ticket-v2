package com.app.footballticketservice.request.response;

import java.sql.ResultSet;
import java.sql.SQLException;

public record MatchListResponse(
        long matchId,
        String matchDate,
        String matchTime,
        int homeGoal,
        int awayGoal,
        String status,
        String widget,
        String homeTeamName,
        String awayTeamName
) {
    public MatchListResponse(ResultSet rs) throws SQLException {
        this(
                rs.getLong("match_id"),
                rs.getString("match_date"),
                rs.getString("match_time"),
                rs.getInt("home_goal"),
                rs.getInt("away_goal"),
                rs.getString("status"),
                rs.getString("widget"),
                rs.getString("home_team_name"),
                rs.getString("away_team_name")
        );
    }
}
