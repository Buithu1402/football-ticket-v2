package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDetailDTO {
    private String name;
    private String logo;
    private String address;
    private String goals;
    private String win;
    private String draw;
    private String losses;
    private String matchPlayed;
    private String establishedYear;
}
