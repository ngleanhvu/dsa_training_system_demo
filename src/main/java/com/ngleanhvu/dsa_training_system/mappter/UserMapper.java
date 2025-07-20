package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.entity.User;

public class UserMapper {
    public static UserResponse toDto(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .build();
    }

    public static UserDetailResponse toDetailDto(User user, UserResponse userResponse) {
        return UserDetailResponse.builder()
                .user(userResponse)
                .dateOfBirth(user.getUserDetails().getDateOfBirth())
                .address(user.getUserDetails().getAddress())
                .firstName(user.getUserDetails().getFirstName())
                .lastName(user.getUserDetails().getLastName())
                .gender(user.getUserDetails().getGender())
                .phoneNumber(user.getUserDetails().getPhoneNumber())
                .githubUrl(user.getUserDetails().getGithubUrl())
                .linkedinUrl(user.getUserDetails().getLinkedinUrl())
                .build();
    }
}
