package com.sdp.notification_service.dto;

import com.sdp.notification_service.model.NotificationStatus;
import com.sdp.notification_service.model.NotificationType;
import com.sdp.notification_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String recipientId;
    private NotificationType type;
    private String title;
    private String content;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private UserRole recipientRole;
}
