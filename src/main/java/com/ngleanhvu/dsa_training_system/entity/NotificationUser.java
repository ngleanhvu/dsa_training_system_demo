package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_user_id")
    private Integer notificationUserId;

    @Column(name = "is_read")
    private boolean isRead;

    @JoinColumn(name = "sender_id", nullable = true)
    @ManyToOne
    private User sender;

    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne
    private User receiver;

    @JoinColumn(name = "notification_id",nullable = false)
    @ManyToOne
    private Notification notification;

    private Integer status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
