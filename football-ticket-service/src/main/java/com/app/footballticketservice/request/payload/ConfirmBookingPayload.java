package com.app.footballticketservice.request.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmBookingPayload {
    private String txnRef;
    private boolean success;
}
