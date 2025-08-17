package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.response.ListNotificationResponse;
import com.ngleanhvu.dsa_training_system.dto.response.NotificationResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;

public interface NotificationService {
    void sendPublicNotification(NotificationResponse payload);
    ListNotificationResponse getNotifications(PagingSearch pagingSearch,
                                              String userId);
    void deleteNotificationByUser(String userId,
                                  Integer notificationId);
    void markRead(String userId,
                  Integer notificationId);
    void sendPrivateNotification(NotificationResponse payload);
    int countNotificationUserIsNotRead(String userId);
}
