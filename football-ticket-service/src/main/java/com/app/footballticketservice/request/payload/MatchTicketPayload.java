package com.app.footballticketservice.request.payload;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.math.BigDecimal;
import java.util.List;

public record MatchTicketPayload(
        Long matchId,
        Long typeId,
        BigDecimal price,
        String description,
        List<Long> seatIds
) {
    public MapSqlParameterSource toMap() {
        return new MapSqlParameterSource()
                .addValue("match_id", matchId)
                .addValue("type_id", typeId)
                .addValue("price", price)
                .addValue("description", description);
    }
}
