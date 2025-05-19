package com.app.footballticketservice.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    SUCCESS(200, "Thành công"),

    BAD_REQUEST(400, "Yêu cầu không hợp lệ"),
    UN_AUTHORIZATION(401, "Yêu cầu xác thực"),
    DENIED_IP(403, "IP không được phép kết nối"),
    PERMISSION_DENIED(403, "Bạn không có quyền truy cập API này"),
    INVALID_INPUT(403, "Dữ liệu đầu vào không hợp lệ"),
    TOO_MANY_REQUEST(403, "Quá nhiều yêu cầu"),
    ACCOUNT_LOCKED(403, "Tài khoản đã bị khóa"),
    ACCOUNT_INACTIVE(403, "Tài khoản cần được kích hoạt"),
    REGISTER_FAILED(403, "Đăng ký thất bại"),
    ACCOUNT_NOT_FOUND(403, "Không tìm thấy tài khoản"),
    PASSWORD_INCORRECT(403, "Mật khẩu không chính xác"),
    SERVER_ERROR(500, "Có lỗi xảy ra với dịch vụ. Vui lòng liên hệ bộ phận Hỗ trợ"),
    ;

    private final int code;
    private final String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonProperty("success")
    public boolean success() {
        return getCode() == ErrorCodeEnum.SUCCESS.getCode();
    }
}
