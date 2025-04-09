package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingHitoryDTO {
    private String txnRef;
    private String ticketType;
    private String teamMatch;
    private String homeLogo;
    private String awayLogo;
    private String matchTime;
    private Long totalPrice;
    private String seats;
    private String bookingStatus;
    private String paymentMethod;
}
