package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.UserUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(String userId);
    UserDetailResponse getProfile(String email);
    void updateUserDetailsByUserId(String email, UserDetailUpdateRequest userDetailUpdateRequest);
    void updateUserByUserId(String userId, UserUpdateRequest userUpdateRequest);
}
