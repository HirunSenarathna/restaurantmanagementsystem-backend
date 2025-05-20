package com.sdp.notification_service.dto;

import com.sdp.notification_service.model.NotificationType;
import com.sdp.notification_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;
    private NotificationType type;
    private String title;
    private String content;
    private UserRole recipientRole;
    private boolean broadcastToRole;
}
