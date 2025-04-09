package com.app.footballticketservice.request.response;


import com.app.footballticketservice.model.User;

public record UserInfoResponse(
        String email,
        String firstName,
        String lastName,
        String gender,
        String avatar
) {
    public UserInfoResponse(User u) {
        this(u.getEmail(), u.getFirstName(), u.getLastName(), u.getGender().name(), u.getAvatar());
    }
}
