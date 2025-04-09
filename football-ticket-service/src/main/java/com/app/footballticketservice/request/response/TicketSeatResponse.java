package com.app.footballticketservice.request.response;

import com.app.footballticketservice.dto.TypeDTO;
import com.app.footballticketservice.model.Seat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record TicketSeatResponse(
        Long typeId,
        String name,
        BigDecimal price,
        String description,
        List<Seat> seats
) {
    public TicketSeatResponse(Map.Entry<Long, List<TypeDTO>> entry) {
        this(
                entry.getKey(),
                entry.getValue().getFirst().getName(),
                entry.getValue().getFirst().getPrice(),
                entry.getValue().getFirst().getDescription(),
                entry.getValue().stream().map(Seat::new).toList()
        );
    }
}
