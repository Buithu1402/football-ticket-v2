package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.request.payload.LoginPayload;
import com.app.footballticketservice.request.payload.OtpVerifyPayload;
import com.app.footballticketservice.request.payload.RegisterPayload;
import com.app.footballticketservice.service.AuthService;
import com.app.footballticketservice.service.EmailService;
import com.app.footballticketservice.service.OtpService;
import com.app.footballticketservice.utils.AESUtils;
import com.app.footballticketservice.utils.CaptchaGenerator;
import com.app.footballticketservice.utils.Constants;
import com.app.footballticketservice.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    private final AuthService authService;
    private final EmailService emailService;
    private final OtpService otpService;

    public AuthenticateController(AuthService authService, EmailService emailService, OtpService otpService) {
        this.authService = authService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @GetMapping(value = "captcha", produces = MediaType.IMAGE_JPEG_VALUE)
    public Object getCaptcha(HttpServletResponse response) {
        var captchaProperty = CaptchaGenerator.getCaptchaProperty();
        var minutes = 60 * 5;
        CookieUtils.add(response, Constants.CAPTCHA, AESUtils.encrypt(captchaProperty.answer()), minutes);
        response.setHeader(Constants.CAPTCHA, AESUtils.encrypt(captchaProperty.answer()));
        return captchaProperty.captcha();
    }

    @PostMapping("login")
    public Object login(
            @Valid @RequestBody LoginPayload payload
    ) {
        return ResponseContainer.success(authService.authenticate(payload.email(), payload.password()));
    }

    @PostMapping("register")
    public Object register(@Valid @RequestBody RegisterPayload payload) {
        try {
            if (authService.validateEmailExist(payload.email())) {
                return ResponseContainer.failure("Email đã tồn tại");
            }
            authService.register(payload);
//            emailService.sendEmailOtp(payload.email());
            return ResponseContainer.success("Đăng ký thành công");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("otp-resend")
    public Object resendOtp(@RequestBody String email) {
        try {
            emailService.sendEmailOtp(email);
            return ResponseContainer.success("Gửi mã OTP thành công");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("forget-password")
    public Object forgetPassword(@RequestBody String email) {
        try {
            var key = "%s|%s".formatted(email, System.currentTimeMillis());
            var encryptedKey = AESUtils.encrypt(key);
            return ResponseContainer.success("Gửi mã OTP thành công");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("otp-verify")
    public Object verifyOtp(@RequestBody OtpVerifyPayload payload) {
        var otp = otpService.getOtp(payload.email());
        if (otp.isEmpty() || otp.get().isNotCorrect(payload.otp())) {
            return ResponseContainer.failure("Mã OTP không hợp lệ");
        }
        if (otp.get().isExpired()) {
            return ResponseContainer.failure("Mã OTP đã hết hạn");
        }
        otpService.deleteOtp(payload.email());
        authService.activeUser(payload.email());
        return ResponseContainer.success("Xác thực OTP thành công");
    }
}
