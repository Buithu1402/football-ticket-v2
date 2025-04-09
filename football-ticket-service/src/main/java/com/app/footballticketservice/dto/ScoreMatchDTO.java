package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreMatchDTO {
    private long matchId;
    private long homeScore;
    private long awayScore;
    private String status;
}
