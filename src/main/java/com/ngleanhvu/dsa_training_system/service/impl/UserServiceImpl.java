package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.UserUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.UserDetailRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;
    private UserDetailRepo userDetailRepo;

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        log.info("User: {}", user);

        UserResponse userResponse = UserResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .build();

        log.info("UserResponse: {}", userResponse);

        return userResponse;
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        log.info("User: {}", user);

        UserResponse userResponse = UserResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .build();

        log.info("UserResponse: {}", userResponse);

        return userResponse;
    }

    @Override
    public UserDetailResponse getProfile(String email) {
        User user = userRepo.findUserDetailsByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        log.info("User: {}", user);

        UserResponse userResponse = UserResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .build();

        UserDetailResponse userDetailResponse = UserDetailResponse.builder()
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

        log.info("UserDetailResponse: {}", userDetailResponse);

        return userDetailResponse;
    }

    @Transactional
    @Override
    public void updateUserDetailsByUserId(String userId, UserDetailUpdateRequest userDetailUpdateRequest) {

        UserDetails userDetails = userDetailRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        log.info("UserDetails: {}", userDetails);

        userDetails.setLastName(userDetailUpdateRequest.getLastName());
        userDetails.setFirstName(userDetailUpdateRequest.getFirstName());
        userDetails.setAddress(userDetailUpdateRequest.getAddress());
        userDetails.setGithubUrl(userDetailUpdateRequest.getGithubUrl());
        userDetails.setLinkedinUrl(userDetailUpdateRequest.getLinkedinUrl());
        userDetails.setPhoneNumber(userDetailUpdateRequest.getPhoneNumber());
        userDetails.setDateOfBirth(userDetailUpdateRequest.getDateOfBirth());
        userDetails.setGender(userDetailUpdateRequest.getGender());

        userDetailRepo.save(userDetails);

    }

    @Override
    public void updateUserByUserId(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        log.info("User: {}", user);

        user.setDisplayName(userUpdateRequest.getDisplayName());

        userRepo.save(user);
    }


}
