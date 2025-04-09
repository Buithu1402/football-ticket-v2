package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticTicketDTO {
    private String matchName;
    private String homeLogo;
    private String awayLogo;
    private Integer totalTicket;
    private Integer totalTicketSold;
    private Integer totalTicketAvailable;
}
