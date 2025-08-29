package com.ngleanhvu.dsa_training_system.websocket;

import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final UserRepo userRepo;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("presend message");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("WebSocketAuthChannelInterceptor preSend: {}", accessor);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("🔍 Processing CONNECT command");

            // Try multiple ways to get the token
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null) {
                authHeader = accessor.getFirstNativeHeader("authorization");
            }

            if (authHeader == null) {
                log.error("❌ Missing Authorization header in WebSocket CONNECT");
                throw new IllegalArgumentException("Missing Authorization header");
            }

            if (!authHeader.startsWith("Bearer ")) {
                log.error("❌ Invalid Authorization header format: {}", authHeader);
                throw new IllegalArgumentException("Invalid Authorization header format");
            }

            String token = authHeader.substring(7);
            log.info("🔑 Extracted token: {}...", token.substring(0, Math.min(20, token.length())));

            try {
                Jwt jwt = jwtDecoder.decode(token);
                String userId = jwt.getSubject();

                log.info("userId from extracted token: {}", userId);

                User user = userRepo.findById(userId).orElse(null);

                if (user == null) {
                    throw new IllegalArgumentException("Failed to convert JWT to Authentication");
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of());

                accessor.setUser(authentication);
                log.info("✅ WebSocket user authenticated: {} with authorities: {}",
                        authentication.getName(), authentication.getAuthorities());

            } catch (Exception e) {
                log.error("❌ Invalid WebSocket JWT: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
            }
        }

        return message;
    }
}