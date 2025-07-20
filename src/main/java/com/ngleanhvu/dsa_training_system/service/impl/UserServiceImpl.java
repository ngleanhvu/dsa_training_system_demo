package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.UserUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.UserMapper;
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

    private final UserRepo userRepo;
    private final UserDetailRepo userDetailRepo;

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        log.info("User: {}", user);

        UserResponse userResponse = UserMapper.toDto(user);

        log.info("UserResponse: {}", userResponse);

        return userResponse;
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        log.info("User: {}", user);

        UserResponse userResponse = UserMapper.toDto(user);

        log.info("UserResponse: {}", userResponse);

        return userResponse;
    }

    @Override
    public UserDetailResponse getProfile(String email) {
        User user = userRepo.findUserDetailsByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        log.info("User: {}", user);

        UserResponse userResponse = UserMapper.toDto(user);

        UserDetailResponse userDetailResponse = UserMapper.toDetailDto(user, userResponse);

        log.info("UserDetailResponse: {}", userDetailResponse);

        return userDetailResponse;
    }

    @Transactional
    @Override
    public void updateUserDetailsByUserId(String userId, UserDetailUpdateRequest userDetailUpdateRequest) {

        UserDetails userDetails = userDetailRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userDetailUpdateRequest.getDisplayName() != null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            user.setDisplayName(userDetailUpdateRequest.getDisplayName());
            userRepo.save(user);
        }

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
