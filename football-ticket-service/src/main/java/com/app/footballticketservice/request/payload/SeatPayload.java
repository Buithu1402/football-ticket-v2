package com.app.footballticketservice.request.payload;

public record SeatPayload(
        long seatId,
        String prefix,
        int start,
        int end,
        long typeId
) {
}
