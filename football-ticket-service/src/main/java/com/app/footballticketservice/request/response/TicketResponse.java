package com.app.footballticketservice.request.response;

import com.app.footballticketservice.dto.TicketDTO;
import com.app.footballticketservice.model.Seat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record TicketResponse(
        Long typeId,
        String name,
        Long matchId,
        BigDecimal price,
        String description,
        List<Seat> seats
) {
    public TicketResponse(Map.Entry<Long, List<TicketDTO>> dtos) {
        this(
                dtos.getValue().getFirst().getTypeId(),
                dtos.getValue().getFirst().getName(),
                dtos.getValue().getFirst().getMatchId(),
                dtos.getValue().getFirst().getPrice(),
                dtos.getValue().getFirst().getDescription(),
                dtos.getValue().stream().map(Seat::new).toList()
        );
    }
}
