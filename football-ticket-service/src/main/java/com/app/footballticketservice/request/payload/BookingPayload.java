package com.app.footballticketservice.request.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPayload {
    private Long matchId;
    private Long typeId;
    private String paymentMethod;
    private List<Long> seatIds;
}
