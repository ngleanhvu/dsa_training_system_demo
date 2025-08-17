package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.response.ListNotificationResponse;
import com.ngleanhvu.dsa_training_system.dto.response.NotificationResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Notification;
import com.ngleanhvu.dsa_training_system.entity.NotificationUser;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.NotificationRepo;
import com.ngleanhvu.dsa_training_system.repo.NotificationUserRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationUserRepo notificationUserRepo;
    private final NotificationRepo notificationRepo;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepo userRepo;

    @Transactional
    @Override
    public void sendPublicNotification(NotificationResponse payload) {
        Notification notification = new Notification();
        notification.setContent(payload.getContent());
        notificationRepo.save(notification);
        payload.setNotificationId(notification.getNotificationId());
        simpMessagingTemplate.convertAndSend("/topic/notifications", payload);
        List<User> users = userRepo.findAll();
        List<NotificationUser> notificationUsers = new ArrayList<>();
        for (User user : users) {
            NotificationUser notificationUser = new NotificationUser();
            notificationUser.setNotification(notification);
            notificationUser.setReceiver(user);
            notificationUser.setRead(false);
            notificationUsers.add(notificationUser);
        }
        notificationUserRepo.saveAll(notificationUsers);
    }



    @Override
    public ListNotificationResponse getNotifications(PagingSearch pagingSearch,
                                                     String userId) {
        Page<NotificationUser> notificationUsers = notificationUserRepo.getNotificationByUserId(userId,
                 pagingSearch.toPageable());

        List<NotificationResponse> notificationResponses = notificationUsers.getContent()
                .stream()
                .map(n -> NotificationResponse.builder()
                        .notificationId(n.getNotification().getNotificationId())
                        .content(n.getNotification().getContent())
                        .isRead(n.isRead())
                        .build())
                .toList();

        return ListNotificationResponse.builder()
                .page(notificationUsers.getNumber()+1)
                .totalPages(notificationUsers.getTotalPages())
                .notifications(notificationResponses)
                .build();
    }

    @Transactional
    @Override
    public void deleteNotificationByUser(String userId, Integer notificationId) {
        Optional<NotificationUser> optionalNotificationUser = notificationUserRepo.findNotificationByUserIdAndNotificationId(userId,
                notificationId);
        if (optionalNotificationUser.isEmpty()) {
            throw new ResourceNotFoundException("Notification user","id", userId);
        }
        NotificationUser notificationUser = optionalNotificationUser.get();
        notificationUserRepo.delete(notificationUser);
    }

    @Transactional
    @Override
    public void markRead(String userId, Integer notificationId) {
        Optional<NotificationUser> optionalNotificationUser = notificationUserRepo.findNotificationByUserIdAndNotificationId(userId,
                notificationId);
        if (optionalNotificationUser.isEmpty()) {
            throw new ResourceNotFoundException("Notification user","id", userId);
        }
        NotificationUser notificationUser = optionalNotificationUser.get();
        notificationUser.setRead(true);
        notificationUserRepo.save(notificationUser);
    }

    @Transactional
    @Override
    public void sendPrivateNotification(NotificationResponse payload) {
        User user = userRepo.findByEmail(payload.getSenderUserName())
                .orElseThrow(() -> new ResourceNotFoundException("User","email", payload.getSenderUserName()));
        Notification notification = new Notification();
        notification.setContent(payload.getContent());
        notificationRepo.save(notification);
        payload.setNotificationId(notification.getNotificationId());
        simpMessagingTemplate.convertAndSendToUser(payload.getSenderUserName(), "/queue/notifications", payload);
        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setNotification(notification);
        notificationUser.setRead(false);
        notificationUser.setReceiver(user);
        notificationUserRepo.save(notificationUser);
    }

    @Override
    public int countNotificationUserIsNotRead(String userId) {
        return notificationUserRepo.countNotificationUserIsNotRead(userId);
    }
}
