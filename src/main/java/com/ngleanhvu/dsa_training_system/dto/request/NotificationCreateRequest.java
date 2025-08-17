package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationCreateRequest {
    private String content;
    private String senderId = null;
    private String receiverId = null;
}
