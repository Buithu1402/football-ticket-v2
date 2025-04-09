package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    private Long typeId;
    private String name;
    private Long matchId;
    private BigDecimal price;
    private String description;
    private Long seatId;
    private String seatNumber;
}
