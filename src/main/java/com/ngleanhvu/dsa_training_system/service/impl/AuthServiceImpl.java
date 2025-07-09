package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.AuthRecord;
import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RegisterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import com.ngleanhvu.dsa_training_system.entity.AuthLocal;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.entity.UserRole;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.AuthLocalRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.AuthService;
import com.ngleanhvu.dsa_training_system.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthLocalRepo authLocalRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final S3Service s3Service;

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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void register(RegisterRequest registerRequest) throws IOException {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmPassword = registerRequest.getConfirmPassword();
        MultipartFile avatar = registerRequest.getAvatar();

        if (authLocalRepo.findByEmail(email).isPresent()) {
            throw new InvalidValueException("Auth local with this email already exists");
        }

        if (!confirmPassword.equals(password)) {
            throw new InvalidValueException("Password not match");
        }

        String avatarUrl = s3Service.upload(avatar);

        String displayName = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;

        String userId = UUID.randomUUID().toString();

        User user = User.builder()
                .status(1)
                .email(email)
                .role(UserRole.USER)
                .avatar(avatarUrl)
                .displayName(displayName)
                .userId(userId)
                .build();

        AuthLocal authLocal = AuthLocal.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .status(1)
                .user(user)
                .build();

        user.setAuthLocal(authLocal);

        userRepo.save(user);
    }

}
