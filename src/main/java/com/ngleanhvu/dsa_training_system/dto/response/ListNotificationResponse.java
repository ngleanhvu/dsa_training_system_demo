package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListNotificationResponse {
    private List<NotificationResponse> notifications;
    private Integer page;
    private Integer totalPages;
}
