package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotNull(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    private String email;
}
