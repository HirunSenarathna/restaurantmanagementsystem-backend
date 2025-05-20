package com.sdp.notification_service.repository;

import com.sdp.notification_service.model.Notification;
import com.sdp.notification_service.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientId(String recipientId);
    List<Notification> findByRecipientRole(UserRole role);
    List<Notification> findByRecipientIdAndStatusNot(String recipientId, String status);
}
