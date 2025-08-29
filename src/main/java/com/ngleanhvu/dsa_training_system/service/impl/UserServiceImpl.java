package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.UserUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListUserResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.UserDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.UserMapper;
import com.ngleanhvu.dsa_training_system.repo.UserDetailRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.repo.spec.UserSpecification;
import com.ngleanhvu.dsa_training_system.service.S3Service;
import com.ngleanhvu.dsa_training_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserDetailRepo userDetailRepo;
    private final S3Service s3Service;


    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserMapper.toDto(user);
    }

    @Override
    public UserDetailResponse getProfile(String email) {
        User user = userRepo.findUserDetailsByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        UserResponse userResponse = UserMapper.toDto(user);
        return UserMapper.toDetailDto(user, userResponse);
    }

    @Transactional
    @Override
    public void updateUserDetailsByUserId(String userId, UserDetailUpdateRequest request) throws IOException {
        log.info("user detail update request: {}", request);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserDetails userDetails = userDetailRepo.findById(userId).orElse(null);
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getAvatar() != null) {
            String avatarUrl = s3Service.upload(request.getAvatar());
            user.setAvatar(avatarUrl);
        }
        userRepo.save(user);
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setUser(user);
        }
        userDetails.setLastName(request.getLastName());
        userDetails.setFirstName(request.getFirstName());
        userDetails.setAddress(request.getAddress());
        userDetails.setGithubUrl(request.getGithubUrl());
        userDetails.setLinkedinUrl(request.getLinkedinUrl());
        userDetails.setPhoneNumber(request.getPhoneNumber());
        userDetails.setDateOfBirth(request.getDateOfBirth());
        userDetails.setGender(UserDetails.Gender.valueOf(request.getGender()));
        userDetailRepo.save(userDetails);
    }


    @Override
    public void updateUserDetailsByEmail(String email, UserDetailUpdateRequest userDetailUpdateRequest) throws IOException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        UserDetails userDetails = userDetailRepo.findByEmail(email).orElse(null);
        if (userDetailUpdateRequest.getDisplayName() != null) {
            user.setDisplayName(userDetailUpdateRequest.getDisplayName());
        }
        if (userDetailUpdateRequest.getAvatar() != null) {
            String avatarUrl = s3Service.upload(userDetailUpdateRequest.getAvatar());
            user.setAvatar(avatarUrl);
        }
        userRepo.save(user);
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setUser(user);
        }
        userDetails.setLastName(userDetailUpdateRequest.getLastName());
        userDetails.setFirstName(userDetailUpdateRequest.getFirstName());
        userDetails.setAddress(userDetailUpdateRequest.getAddress());
        userDetails.setGithubUrl(userDetailUpdateRequest.getGithubUrl());
        userDetails.setLinkedinUrl(userDetailUpdateRequest.getLinkedinUrl());
        userDetails.setPhoneNumber(userDetailUpdateRequest.getPhoneNumber());
        userDetails.setDateOfBirth(userDetailUpdateRequest.getDateOfBirth());
        userDetails.setGender(UserDetails.Gender.valueOf(userDetailUpdateRequest.getGender().toUpperCase()));
        userDetailRepo.save(userDetails);
    }


    @Override
    public void updateUserByUserId(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setDisplayName(userUpdateRequest.getDisplayName());
        userRepo.save(user);
    }

    @Override
    public ListUserResponse getUsers(String keyword, PagingSearch pagingSearch) {
        Specification<User> specification = UserSpecification.hasKeyword(keyword);
        Page<User> userPage = userRepo.findAll(specification, pagingSearch.toPageable());
        List<UserResponse> userResponses = userPage
                .getContent().stream().map(UserMapper::toDto)
                .toList();
        return ListUserResponse.builder()
                .users(userResponses)
                .page(userPage.getNumber()+1)
                .totalPages(userPage.getTotalPages())
                .build();
    }


}
