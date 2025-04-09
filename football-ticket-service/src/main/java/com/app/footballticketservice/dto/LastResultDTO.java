package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastResultDTO {
    private String homeTeam;
    private String awayTeam;
    private String homeScore;
    private String awayScore;
    private String matchDatetime;
    private String leagueName;
    private String homeLogo;
    private String awayLogo;
    private String status;
}
