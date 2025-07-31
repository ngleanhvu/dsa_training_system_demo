package com.ngleanhvu.dsa_training_system.dto.response;

import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDetailResponse {
    private UserResponse user;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private UserDetails.Gender gender;
    private String role;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
}
