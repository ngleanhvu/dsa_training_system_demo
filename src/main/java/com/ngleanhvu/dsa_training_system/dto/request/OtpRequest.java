package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpRequest {
    @NotNull(message = "Vui lòng nhập otp")
    @Pattern(regexp = "\\d{6}", message = "OTP phải có 6 chữ số")
    private String otp;
    @NotNull(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    private String email;
}
