package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.entity.UserDetails;

public class UserMapper {
    public static UserResponse toDto(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .role(user.getRole().name())
                .build();
    }

    public static UserDetailResponse toDetailDto(User user, UserResponse userResponse) {
        if (user.getUserDetails() == null) {
            return UserDetailResponse.builder()
                    .user(userResponse)
                    .dateOfBirth(null)
                    .address("")
                    .firstName("")
                    .lastName("")
                    .gender(UserDetails.Gender.OTHER.name())
                    .phoneNumber("")
                    .githubUrl("")
                    .linkedinUrl("")
                    .build();
        }


        return UserDetailResponse.builder()
                .user(userResponse)
                .dateOfBirth(user.getUserDetails().getDateOfBirth())
                .address(user.getUserDetails().getAddress())
                .firstName(user.getUserDetails().getFirstName())
                .lastName(user.getUserDetails().getLastName())
                .gender(user.getUserDetails().getGender().name())
                .phoneNumber(user.getUserDetails().getPhoneNumber())
                .githubUrl(user.getUserDetails().getGithubUrl())
                .linkedinUrl(user.getUserDetails().getLinkedinUrl())
                .build();
    }

}
