package com.app.footballticketservice.service;

import com.app.footballticketservice.exception.AppException;
import com.app.footballticketservice.jwt.JwtTokenProvider;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.model.enums.ErrorCodeEnum;
import com.app.footballticketservice.model.enums.RoleEnum;
import com.app.footballticketservice.model.enums.StatusEnum;
import com.app.footballticketservice.repo.UserRepo;
import com.app.footballticketservice.request.payload.RegisterPayload;
import com.app.footballticketservice.request.response.LoginResponse;
import com.app.footballticketservice.security.PasswordEncodeImpl;
import com.app.footballticketservice.utils.AESUtils;
import com.app.footballticketservice.utils.Constants;
import com.app.footballticketservice.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncodeImpl passwordEncode;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepo userRepo, PasswordEncodeImpl passwordEncode,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepo = userRepo;
        this.passwordEncode = passwordEncode;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse authenticate(String email, String password) {
        var user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new AppException(ErrorCodeEnum.ACCOUNT_NOT_FOUND);
        }
        validateLogin(user.get(), password);
        var userToken = jwtTokenProvider.generateToken(user);
        return new LoginResponse(
                userToken,
                "",
                RoleEnum.ADMIN.equals(user.get().getRole()),
                true
        );
    }

    public void register(RegisterPayload payload) {
        var user = new User();
        user.setEmail(payload.email());
        user.setPassword(passwordEncode.encode(payload.password()));
        user.setFirstName(payload.firstName());
        user.setLastName(payload.lastName());
        user.setRole(RoleEnum.USER);
        user.setStatus(StatusEnum.ACTIVE);
        userRepo.save(user, true);
    }

    public void activeUser(String email) {
        userRepo.activateUser(email);
    }

    public boolean validateEmailExist(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public boolean validateCaptcha(HttpServletRequest request, String captcha) {
        var cookie = CookieUtils.get(request, Constants.CAPTCHA);
        return cookie
                .filter(value -> value.getValue() != null)
                .map(value -> AESUtils.decrypt(value.getValue()).equals(captcha.trim()))
                .orElseGet(() -> AESUtils.decrypt(StringUtils.defaultIfBlank(
                                                 request.getHeader(Constants.CAPTCHA),
                                                 StringUtils.EMPTY
                                         ))
                                         .equals(captcha.trim()));
    }

    private void validateLogin(User user, String password) {
        if (!passwordEncode.matches(password, user.getPassword())) {
            throw new AppException(ErrorCodeEnum.PASSWORD_INCORRECT);
        }
        if (StatusEnum.BLOCKED.equals(user.getStatus())) {
            throw new AppException(ErrorCodeEnum.ACCOUNT_LOCKED);
        } else if (StatusEnum.INACTIVE.equals(user.getStatus())) {
            throw new AppException(ErrorCodeEnum.ACCOUNT_INACTIVE);
        }
    }
}
