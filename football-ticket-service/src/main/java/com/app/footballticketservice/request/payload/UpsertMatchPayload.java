package com.app.footballticketservice.request.payload;

import com.app.footballticketservice.model.Match;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertMatchPayload extends Match {
    private String link;
}
