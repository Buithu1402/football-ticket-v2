package com.app.footballticketservice.model;

import com.app.footballticketservice.model.enums.GenderEnum;
import com.app.footballticketservice.model.enums.RoleEnum;
import com.app.footballticketservice.model.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class User {
    long userId;
    String email;
    String password;
    String firstName;
    String lastName;

    @Builder.Default
    GenderEnum gender = GenderEnum.OTHER;
    String avatar;
    @Builder.Default
    RoleEnum role = RoleEnum.USER;
    @Builder.Default
    StatusEnum status = StatusEnum.INACTIVE;

    public User(long userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public User(ResultSet rs) throws SQLException {
        this(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                GenderEnum.valueOf(rs.getString("gender").toUpperCase()),
                rs.getString("avatar"),
                RoleEnum.valueOf(rs.getString("role").toUpperCase()),
                StatusEnum.valueOf(rs.getString("status").toUpperCase())
        );
    }
}
