package com.ngleanhvu.dsa_training_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Cấu hình endpoint cho client kết nối vào
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Đây là endpoint phía client sẽ connect
                .setAllowedOrigins("*") // Cho phép mọi origin kết nối
                .withSockJS(); // Dùng SockJS fallback nếu không hỗ trợ WebSocket
    }

    // Cấu hình message broker
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Prefix cho bên gửi từ server đến client
        registry.setApplicationDestinationPrefixes("/app"); // Prefix cho bên client gửi đến server
    }
}
