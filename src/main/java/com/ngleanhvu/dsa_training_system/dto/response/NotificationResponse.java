package com.ngleanhvu.dsa_training_system.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Integer notificationId;
    private String content;
    private String senderUserName;
    private boolean isRead;
}
