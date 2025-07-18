package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.AuthRecord;
import com.ngleanhvu.dsa_training_system.dto.request.EmailRecord;
import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RegisterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import com.ngleanhvu.dsa_training_system.entity.AuthLocal;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.entity.UserRole;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.redis.EmailConfirmTokenService;
import com.ngleanhvu.dsa_training_system.redis.RedisKey;
import com.ngleanhvu.dsa_training_system.repo.AuthLocalRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.AuthService;
import com.ngleanhvu.dsa_training_system.service.EmailService;
import com.ngleanhvu.dsa_training_system.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthLocalRepo authLocalRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final S3Service s3Service;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EmailConfirmTokenService emailConfirmTokenService;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Value("${jwt.expiration.refresh_token}")
    private long refreshTokenExpiration;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address}")
    private String serverAddress;

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

        String jti = jwtUtil.getJti(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Refresh token does not exist"));

        Date expiration = jwtUtil.getExpiration(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Refresh token does not exist"));

        stringRedisTemplate.opsForValue().set(RedisKey.generateRefreshKey(authRecord.userid(), jti), String.valueOf(expiration.getTime()), refreshTokenExpiration, TimeUnit.SECONDS);

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

        String token = UUID.randomUUID().toString();
        emailConfirmTokenService.saveToken(token, userId, Duration.ofMinutes(15));

        EmailRecord emailRecord = getEmailRecord(token, email);

        String json = objectMapper.writeValueAsString(emailRecord);

        kafkaTemplate.send(KafkaConst.AUTH_CREATE_TOPIC, email, json);
    }

    private EmailRecord getEmailRecord(String token, String email) {
        String confirmationLink = String.format("http://%s:%s/api/v1/auths/confirm?token=%s", serverAddress, serverPort, token);

        String subject = "Confirm your account registration";
        String body = String.format("""
                    Hello,
                
                    Thank you for registering! Please confirm your email by clicking the link below:
                
                    %s
                
                    This link will expire in 15 minutes.
                
                    Best regards,
                    YourApp Team
                    """, confirmationLink);

        EmailRecord emailRecord = new EmailRecord(email, subject, body);

        log.info("Email record: {}", emailRecord);

        return emailRecord;
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        String jti = jwtUtil.getJti(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Invalid refresh token"));

        String blackListKey = RedisKey.generateBlackListKey(jti);

        if (stringRedisTemplate.hasKey(blackListKey)) {
            throw new InvalidValueException("Refresh revoked");
        }

        String authId = jwtUtil.getSubject(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Invalid refresh token"));

        AuthLocal authLocal = authLocalRepo.findById(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Auth local","email",authId));

        User user = authLocal.getUser();

        AuthRecord authRecord = new AuthRecord(user.getUserId(), user.getRole());

        return LoginResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(authRecord))
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        String jti = jwtUtil.getJti(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Refresh token does not exist"));

        String authId = jwtUtil.getSubject(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Refresh token does not exist"));

        Date expiration = jwtUtil.getExpiration(refreshToken)
                .orElseThrow(() -> new InvalidValueException("Refresh token does not exist"));

        long ttl = expiration.getTime() - System.currentTimeMillis() > 0 ? expiration.getTime() : 0;

        String key = RedisKey.generateRefreshKey(authId, jti);

        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
            stringRedisTemplate.opsForValue().set(RedisKey.generateBlackListKey(jti), String.valueOf(1), ttl, TimeUnit.SECONDS);
        }
    }

    @KafkaListener(topics = KafkaConst.AUTH_CREATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void sendEmailForRegister(String json) throws JsonProcessingException {
        EmailRecord emailRecord = objectMapper.readValue(json, EmailRecord.class);
        emailService.sendEmail(emailRecord.to(), emailRecord.subject(), emailRecord.body());
    }



}
