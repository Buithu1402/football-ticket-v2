package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.request.response.UserInfoResponse;
import com.app.footballticketservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseContainer.success(userService.getAll(page, size));
    }

    @GetMapping("info")
    public Object info(@AuthenticationPrincipal User user) {
        return ResponseContainer.success(new UserInfoResponse(user));
    }

    @PatchMapping("manage")
    public Object manage(
            @RequestParam("uid") long uid,
            @RequestParam("action") String action
    ) {
        userService.manage(uid, action);
        return ResponseContainer.success("OK");
    }
}
