package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.NotificationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationUserRepo extends JpaRepository<NotificationUser, Integer> {
    @Query("SELECT nu FROM NotificationUser nu WHERE nu.receiver.userId = :userId")
    Page<NotificationUser> getNotificationByUserId (@Param("userId") String userId,
                                                    Pageable pageable);

    @Query("SELECT nu FROM NotificationUser nu WHERE nu.receiver.userId = :userId AND nu.notification.notificationId = :notificationId")
    Optional<NotificationUser> findNotificationByUserIdAndNotificationId(@Param("userId") String userId,
                                                                         @Param("notificationId") Integer notificationId);

    @Query("SELECT COUNT(nu) FROM NotificationUser nu WHERE nu.receiver.userId = :userId AND nu.isRead = false")
    int countNotificationUserIsNotRead(@Param("userId") String userId);

}
