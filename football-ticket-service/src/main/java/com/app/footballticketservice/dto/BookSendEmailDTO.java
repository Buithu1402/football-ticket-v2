package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookSendEmailDTO {
    private String seats;
    private String homeTeam;
    private String awayTeam;
    private String homeLogo;
    private String awayLogo;
    private String matchDate;
    private String matchTime;
    private String stadium;
}
