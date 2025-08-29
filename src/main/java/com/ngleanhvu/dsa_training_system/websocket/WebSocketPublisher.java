package com.ngleanhvu.dsa_training_system.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSubmissionUpdate(String email, Object payload) {
        try {
            String destination = "/queue/submissions";
            messagingTemplate.convertAndSendToUser(email, destination, payload);
        } catch (Exception e) {
            log.error("Error sending WebSocket message to user: {}", email, e);
        }
    }

}