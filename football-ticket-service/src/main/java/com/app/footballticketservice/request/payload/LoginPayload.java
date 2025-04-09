package com.app.footballticketservice.request.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record LoginPayload(
        @NotEmpty(message = "Email is required") @Email(message = "Email is invalid") String email,
        @Length(min = 6, message = "Password must be at least 6 character") String password,
        @Length(min = 4, max = 4, message = "Captcha bao gồm 4 ký tự") String captcha
) {
}
