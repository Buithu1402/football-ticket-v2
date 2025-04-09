package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTicketSeatDTO {
    private Long matchId;
    private Long seatId;
    private String seatNumber;
    private String isBooked;
    private BigDecimal price;

    public boolean available() {
        return "yes".equalsIgnoreCase(isBooked);
    }
}
