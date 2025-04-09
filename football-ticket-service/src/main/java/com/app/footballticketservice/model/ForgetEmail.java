package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ForgetEmail {
    private String email;
    private String link;
}
