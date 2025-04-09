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
public class Booking {
    long bookingId;
    long ticketId;
    long userId;
    int quantity;
    BigDecimal total;
    String status;

    public Booking(ResultSet rs) throws SQLException {
        this(
                rs.getLong("booking_id"),
                rs.getLong("ticket_id"),
                rs.getLong("user_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("total"),
                rs.getString("status")
        );
    }
}
