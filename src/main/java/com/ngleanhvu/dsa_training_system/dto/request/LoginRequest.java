package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email không đúng định dạng")
    @NotNull(message = "Email trống")
    private String email;
    @NotNull(message = "Mật khẩu trống")
    @Pattern(regexp = "^.{8,}$", message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;
    private String role;
}
