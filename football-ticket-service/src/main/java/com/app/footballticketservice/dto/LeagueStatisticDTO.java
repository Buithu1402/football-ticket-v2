package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeagueStatisticDTO {
    private String name;
    private String leagueLogo;
    private String matchCount;
    private String completedMatchCount;
    private String pendingMatchCount;
    private String liveMatchCount;
}
