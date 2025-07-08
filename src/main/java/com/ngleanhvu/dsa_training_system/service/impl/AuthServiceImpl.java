package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.AuthRecord;
import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import com.ngleanhvu.dsa_training_system.entity.AuthLocal;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.AuthLocalRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthLocalRepo authLocalRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws InvalidCredentialsException {
        AuthLocal authLocal = authLocalRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Auth local","email",loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), authLocal.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        User user = userRepo.findById(authLocal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User","id",authLocal.getUserId()));

        AuthRecord authRecord = new AuthRecord(user.getUserId(), user.getRole());

        String accessToken = jwtUtil.generateAccessToken(authRecord);
        String refreshToken = jwtUtil.generateRefreshToken(authRecord);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
