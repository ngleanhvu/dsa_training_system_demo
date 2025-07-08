package com.ngleanhvu.dsa_training_system.security;

import com.ngleanhvu.dsa_training_system.dto.request.AuthRecord;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
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

    public Optional<Date> getIssuedAt(String token) {
        return parseClaims(token).map(Claims::getIssuedAt);
    }

}
