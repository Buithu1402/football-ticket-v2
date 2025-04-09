package com.app.footballticketservice.request.payload;


import java.util.List;

public record StadiumPayload(
        long stadiumId,
        String stadiumName,
        String address,
        List<SeatPayload> seats
) {
}
