package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotNull(message = "Vui lòng nhập mật khẩu")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    @NotNull(message = "Vui lòng nhập xác nhận mật khẩu")
    @Size(min = 8, message = "Xác nhận mật khẩu phải có ít nhất 8 ký tự")
    private String confirmPassword;

    @NotNull(message = "Vui lòng nhập otp")
    @Pattern(regexp = "\\d{6}", message = "OTP phải có 6 chữ số")
    private String otp;

    @NotNull(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    private String email;
}
