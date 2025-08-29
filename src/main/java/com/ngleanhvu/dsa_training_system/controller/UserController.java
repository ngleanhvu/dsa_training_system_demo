package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.UserDetailUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.UserResponse;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.UserService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
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

    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(required = false, defaultValue = "") String keyword,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "userId") String sortBy,
                                      @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDirection, Math.max(page-1,0), pageSize);
        var response = userService.getUsers(keyword, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .message("Get users success")
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

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

    @PreAuthorize("hasAnyRole('USER')")
    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @ModelAttribute UserDetailUpdateRequest userDetailUpdateRequest,
                                           @RequestHeader("Authorization") String token) throws IOException {

        String userId = jwtUtil.getUserIdFromToken(token);
        userService.updateUserDetailsByUserId(userId, userDetailUpdateRequest);
        var apiResponse = ApiResponse.builder()
                .metadata(null)
                .message("Update user success")
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping(value = "/update/{email}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute UserDetailUpdateRequest userDetailUpdateRequest,
                                        @PathVariable("email") String email) throws IOException {
        userService.updateUserDetailsByEmail(email, userDetailUpdateRequest);
        var apiResponse = ApiResponse.builder()
                .metadata(null)
                .message("Update user success")
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
