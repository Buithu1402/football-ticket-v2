package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stadium {
    private Long stadiumId;
    private String stadiumName;
    private String image;
    private String address;
}
