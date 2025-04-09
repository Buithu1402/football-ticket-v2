package com.app.footballticketservice.model;

import com.app.footballticketservice.dto.TicketDTO;
import com.app.footballticketservice.dto.TypeDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat {
    Long seatId;
    Long stadiumId;
    String seatNumber;
    Long typeId;
    String typeName;

    public Seat(TicketDTO dto) {
        this(
                dto.getSeatId(),
                -1L,
                dto.getSeatNumber(),
                -1L,
                ""
        );
    }

    public Seat(TypeDTO dto) {
        this(
                dto.getSeatId(),
                -1L,
                dto.getSeatNumber(),
                -1L,
                ""
        );
    }
}
