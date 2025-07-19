package com.ngleanhvu.dsa_training_system.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RegisterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import org.apache.http.auth.InvalidCredentialsException;

import java.io.IOException;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest) throws InvalidCredentialsException;
    void register(RegisterRequest registerRequest) throws IOException;
    LoginResponse refresh(String refreshToken);
    void logout(String refreshToken);
    LoginResponse loginWithGoogle(GoogleIdToken.Payload payload);
    LoginResponse loginWithGithub(String accessToken);
}
