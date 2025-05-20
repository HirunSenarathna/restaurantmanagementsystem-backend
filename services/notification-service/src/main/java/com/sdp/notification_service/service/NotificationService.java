package com.sdp.notification_service.service;

import com.sdp.notification_service.client.UserServiceClient;
import com.sdp.notification_service.dto.UserResponse;
import com.sdp.notification_service.exception.NotificationException;
import com.sdp.notification_service.model.Notification;
import com.sdp.notification_service.model.NotificationStatus;
import com.sdp.notification_service.model.UserRole;
import com.sdp.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    private final SmsService smsService;
    private final WebSocketService webSocketService;

    @Transactional
    public Notification sendNotification(Notification notification) {
        try {
            notification.setStatus(NotificationStatus.CREATED);
            Notification savedNotification = notificationRepository.save(notification);

            if (notification.getRecipientEmail() != null) {
                emailService.sendEmail(notification);
            }

            if (notification.getRecipientPhone() != null) {
                smsService.sendSms(notification);
            }

            if (notification.getRecipientId() != null) {
                webSocketService.sendToUser(notification);
            }

            savedNotification.setStatus(NotificationStatus.SENT);
            return notificationRepository.save(savedNotification);

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            throw new NotificationException("Failed to send notification", e);
        }
    }

    @Transactional
    public void broadcastToRole(Notification notification, UserRole role) {
        try {
            List<UserResponse> users = userServiceClient.getUsersByRole(UserRole.valueOf(role.name()));

            for (UserResponse user : users) {
                Notification personalNotification = new Notification(
                        String.valueOf(user.getId()),
                        user.getEmail(),
                        user.getPhone(),
                        notification.getType(),
                        notification.getTitle(),
                        notification.getContent(),
                        role
                );
                sendNotification(personalNotification);
            }
        } catch (Exception e) {
            log.error("Failed to broadcast to role {}: {}", role, e.getMessage());
            throw new NotificationException("Failed to broadcast notification to role: " + role, e);
        }
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByRecipientId(userId);
    }

    public List<Notification> getUnreadUserNotifications(String userId) {
        return notificationRepository.findByRecipientIdAndStatusNot(userId, NotificationStatus.READ.name());
    }

    public List<Notification> getNotificationsByRole(UserRole role) {
        return notificationRepository.findByRecipientRole(role);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("Notification not found with id: " + notificationId));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = getUnreadUserNotifications(userId);
        for (Notification notification : unreadNotifications) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotificationException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }
}
