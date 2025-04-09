package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeagueDTO {
    private Long leagueId;
    private String logo;
    private String name;
    private Long matchCount;
    private Long matchToday;
    private Long matchTomorrow;
    private Long matchLive;
    private Long matchPending;
    private BigDecimal minPrice;
}
