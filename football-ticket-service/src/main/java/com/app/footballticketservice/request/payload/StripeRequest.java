package com.app.footballticketservice.request.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeRequest {
    @Builder.Default
    String description = "Payment for football ticket";
    Long amount;
    String name;

    @Builder.Default
    Long quantity = 1L;

    @Builder.Default
    Currency currency = Currency.VND;
    String stripeEmail;
    String stripeToken;

    public StripeRequest(String email, long amount, String name) {
        this.amount = amount;
        this.name = name;
        this.stripeEmail = email;
    }

    public enum Currency {
        EUR, USD, VND
    }
}
