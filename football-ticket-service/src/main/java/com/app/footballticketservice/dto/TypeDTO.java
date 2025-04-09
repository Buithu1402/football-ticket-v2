package com.app.footballticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeDTO {
    private Long typeId;
    private String name;
    private BigDecimal price;
    private String description;
    private Long seatId;
    private String seatNumber;
}
