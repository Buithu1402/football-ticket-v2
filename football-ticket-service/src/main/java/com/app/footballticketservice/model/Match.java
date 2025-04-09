package com.app.footballticketservice.model;

import com.app.footballticketservice.utils.JDBCUtils;
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
public class Match {
    long matchId;
    long leagueId;
    long homeTeamId;
    long awayTeamId;
    int homeGoals;
    int awayGoals;
    String matchDate;
    String matchTime;
    String status;
    String widget;
    long stadiumId;

    public Match(ResultSet rs) throws SQLException {
        this(
                rs.getLong("match_id"),
                rs.getLong("league_id"),
                rs.getLong("home_id"),
                rs.getLong("away_id"),
                rs.getInt("home_goal"),
                rs.getInt("away_goal"),
                rs.getString("match_date"),
                rs.getString("match_time"),
                rs.getString("status"),
                rs.getString("widget"),
                JDBCUtils.getValueResultSet(rs, Long.class, 0L, "stadium_id")
        );
    }
}
