package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeagueRevenueDTO {
    private String leagueName;
    private BigDecimal totalRevenue;
}
