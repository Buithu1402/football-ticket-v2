package com.app.footballticketservice.request.payload;

import java.util.List;

public record MatchListPayload(
        Integer page,
        Integer size,
        String key,
        String date,
        Long leagueId,
        List<String> status
) {
}
