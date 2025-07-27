package com.ngleanhvu.dsa_training_system.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.ngleanhvu.dsa_training_system.dto.request.LoginRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RefreshRequest;
import com.ngleanhvu.dsa_training_system.dto.request.RegisterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.LoginResponse;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import com.ngleanhvu.dsa_training_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auths")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RestTemplate restTemplate;

    @Value("${google.client_id}")
    private String googleClientId;

    @Value("${github.client_id}")
    private String githubClientId;

    @Value("${github.client_secret}")
    private String githubClientSecret;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {
        var response = authService.login(loginRequest);
        var apiResponse = ApiResponse.builder()
                .message("Đăng nhập thành công!")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) throws InvalidCredentialsException, PermissionException {
        var response = authService.loginWithAdminAccount(loginRequest);
        var apiResponse = ApiResponse.builder()
                .message("Đăng nhập thành công!")
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

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        GoogleIdToken.Payload payload = verifyGoogleToken(idToken);

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
        }

        LoginResponse loginResponse = authService.loginWithGoogle(payload);

        var apiResponse = ApiResponse.builder()
                .message("Login success")
                .status(HttpStatus.OK.name())
                .metadata(loginResponse)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/github/login")
    public ResponseEntity<?> githubLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        String accessToken = getAccessToken(code);
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
        }

        LoginResponse loginResponse = authService.loginWithGithub(accessToken);

        var apiResponse = ApiResponse.builder()
                .message("Login success")
                .status(HttpStatus.OK.name())
                .metadata(loginResponse);

        log.debug("apiResponse: {}", apiResponse);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    private String getAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> params = Map.of(
                "client_id", githubClientId,
                "client_secret", githubClientSecret,
                "code", code
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://github.com/login/oauth/access_token", request, Map.class);

        return response.getBody() != null ? (String) response.getBody().get("access_token") : null;
    }


    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;

        } catch (Exception e) {
            log.error("error while verifying google id token", e);
            return null;
        }
    }

}
