package com.ngleanhvu.dsa_training_system.dto.request;

import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDetailUpdateRequest {
    private String displayName;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private UserDetails.Gender gender;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
}
