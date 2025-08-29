package com.ngleanhvu.dsa_training_system.security;

import com.ngleanhvu.dsa_training_system.dto.request.AuthRecord;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.expiration.access_token}")
    private long EXPIRATION_ACCESS_KEY;

    @Value("${jwt.expiration.refresh_token}")
    private long EXPIRATION_SECRET_KEY;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        privateKey = RsaKeyUtil.getPrivateKey();
        publicKey = RsaKeyUtil.getPublicKey();
    }

    public String generateAccessToken(AuthRecord authRecord) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(String.valueOf(authRecord.userid()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(EXPIRATION_ACCESS_KEY)))
                .claim("role", authRecord.userRole().name())
                .claim("jti", UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(AuthRecord authRecord) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claim("jti", UUID.randomUUID().toString())
                .setSubject(String.valueOf(authRecord.userid()))
                .claim("type", "refresh_token")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(EXPIRATION_SECRET_KEY)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Optional<Claims> parseClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> getClaim(String token, Function<Claims, String> resolver) {
        return parseClaims(token).map(resolver);
    }

    public Optional<String> getSubject(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Optional<String> getJti(String token) {
        return getClaim(token, claims -> String.valueOf(claims.get("jti")));
    }

    public Optional<Date> getExpiration(String token) {
        return parseClaims(token).map(Claims::getExpiration);
    }

    public String getUserIdFromToken(@RequestHeader("Authorization")  String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        String userId = this.getSubject(token)
                .orElseThrow(() -> new RuntimeException("Invalid token: subject is missing"));

        log.debug("userId = {}", userId);

        return userId;
    }
}
