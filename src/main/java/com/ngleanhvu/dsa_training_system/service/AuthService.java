package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import org.apache.http.auth.InvalidCredentialsException;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest) throws InvalidCredentialsException;
}
