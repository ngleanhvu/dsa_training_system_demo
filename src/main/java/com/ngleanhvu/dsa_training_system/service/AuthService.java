package com.ngleanhvu.dsa_training_system.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RegisterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import org.apache.http.auth.InvalidCredentialsException;

import java.io.IOException;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest) throws InvalidCredentialsException;
    void register(RegisterRequest registerRequest) throws IOException;
    LoginResponse refresh(String refreshToken);
    void logout(String refreshToken);
    LoginResponse loginWithGoogle(GoogleIdToken.Payload payload);
    LoginResponse loginWithGithub(String accessToken);
    LoginResponse loginWithAdminAccount(LoginRequest loginRequest) throws InvalidCredentialsException, PermissionException;
    void forgotPassword(String email);
    void verifyOtp(String otp,
                   String email);
    void resetPassword(String newPassword,
                       String confirmPassword,
                       String email,
                       String otp);
}
