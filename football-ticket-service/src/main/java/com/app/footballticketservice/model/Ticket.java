package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Ticket {
    long ticketId;
    long matchId;
    int quantity;
    BigDecimal price;
    String type;
    String status;

    public Ticket(ResultSet rs) throws SQLException {
        this(
                rs.getLong("ticket_id"),
                rs.getLong("match_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price"),
                rs.getString("type"),
                rs.getString("status")
        );
    }
}
