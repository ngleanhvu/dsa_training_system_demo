package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        UserResponse userResponse = userService.getUserByEmail(email);
        var apiResponse = ApiResponse.builder()
                .metadata(userResponse)
                .message("Get user success")
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/profile/{email}")
    public ResponseEntity<?> getProfile(@PathVariable("email") String email) {


        var response = userService.getProfile(email);

        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .message("Get profile success")
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UserDetailUpdateRequest userDetailUpdateRequest,
                                        @RequestHeader("Authorization") String token) {
        String userId = String.valueOf(jwtUtil.getSubject(token));

        userService.updateUserDetailsByUserId(userId, userDetailUpdateRequest);

        var apiResponse = ApiResponse.builder()
                .metadata(null)
                .message("Update user success")
                .status(HttpStatus.OK.name())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
