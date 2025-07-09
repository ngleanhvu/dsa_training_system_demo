package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private MultipartFile avatar;
}
