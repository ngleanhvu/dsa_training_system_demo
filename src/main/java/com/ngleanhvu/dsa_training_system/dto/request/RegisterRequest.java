package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterRequest {
    @NotNull(message = "Vui lòng nhập email")
    @Email(message = "Vui lòng nhập email đúng định dạng")
    private String email;
    @NotNull(message = "Vui lòng nhập mật khẩu")
    private String password;
    @NotNull(message = "Vui lòng nhập xác nhận mật khẩu")
    private String confirmPassword;
    @NotNull(message = "Vui lòng upload avatar")
    private MultipartFile avatar;
}
