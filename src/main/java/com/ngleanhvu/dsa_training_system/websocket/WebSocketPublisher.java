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
            log.info("Sending WebSocket message to user: {} at destination: {}", email, destination);
            log.info("Payload: {}", payload);

            messagingTemplate.convertAndSendToUser(email, destination, payload);

            log.info("WebSocket message sent successfully");
        } catch (Exception e) {
            log.error("Error sending WebSocket message to user: {}", email, e);
        }
    }

    public void sendProblemCreation(Object payload) {
        String destination = "/topic/notifications";
        messagingTemplate.convertAndSend(destination, payload);
    }
}