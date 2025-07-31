package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.UserUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListUserResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;

import java.io.IOException;

public interface UserService {
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(String userId);
    UserDetailResponse getProfile(String email);
    void updateUserDetailsByUserId(String userId, UserDetailUpdateRequest userDetailUpdateRequest) throws IOException;
    void updateUserDetailsByEmail(String email, UserDetailUpdateRequest userDetailUpdateRequest) throws IOException;
    void updateUserByUserId(String userId, UserUpdateRequest userUpdateRequest);
    ListUserResponse getUsers(String keyword, PagingSearch pagingSearch);
}
