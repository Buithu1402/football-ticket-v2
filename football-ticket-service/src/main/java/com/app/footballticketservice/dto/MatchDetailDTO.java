package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailDTO {
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private String homeLogo;
    private String awayLogo;
    private Integer homeGoal;
    private Integer awayGoal;
    private Date matchDate;
    private Time matchTime;
    private String leagueName;
    private String stadium;
    private String stadiumImage;
    private String matchStatus;
}
