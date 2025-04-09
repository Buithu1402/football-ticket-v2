package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.request.payload.BookingPayload;
import com.app.footballticketservice.request.payload.ConfirmBookingPayload;
import com.app.footballticketservice.service.BookingService;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public Object getBooking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "key", defaultValue = "") String key
    ) {
        return ResponseContainer.success(bookingService.getBooking( page, size, key));
    }

    @GetMapping("history")
    public Object history(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseContainer.success(bookingService.history(user, page, size));
    }

    @PostMapping("confirm")
    public Object confirm(
            @RequestBody ConfirmBookingPayload payload,
            @AuthenticationPrincipal User user
    ) throws MessagingException {
        bookingService.confirmBooking(payload.getTxnRef(), payload.isSuccess(), user);
        return ResponseContainer.success("success");
    }

    @PostMapping
    public Object bookTicket(
            @RequestBody BookingPayload payload,
            @AuthenticationPrincipal User user
    ) throws StripeException {
        return ResponseContainer.success(bookingService.bookTicket(payload, user));
    }
}
