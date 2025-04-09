package com.app.footballticketservice.request.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isAdmin,
        boolean valid
) {
}
