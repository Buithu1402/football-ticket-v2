package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MatchDTO {
    int matchId;
    int leagueId;
    int homeTeamId;
    String homeTeamName;
    String homeLogo;
    int awayTeamId;
    String awayTeamName;
    String awayLogo;
    int homeGoal;
    int awayGoal;
    String matchDate;
    String matchTime;
    String status;
    String leagueName;
    String stadiumName;
}
