package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RefreshRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RegisterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auths")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {
        var response = authService.login(loginRequest);
        var apiResponse = ApiResponse.builder()
                .message("Login success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RegisterRequest registerRequest) throws IOException {
        authService.register(registerRequest);
        var apiResponse = ApiResponse.builder()
                .message("Register success")
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        var response = authService.refresh(refreshRequest.getRefreshToken());
        var apiResponse = ApiResponse.builder()
                .message("Refresh success")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<?> logout(@RequestBody RefreshRequest refreshRequest) {
        authService.logout(refreshRequest.getRefreshToken());
        var apiResponse = ApiResponse.builder()
                .message("Logout success")
                .status(HttpStatus.OK.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
