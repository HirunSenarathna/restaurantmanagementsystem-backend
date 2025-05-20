package com.sdp.notification_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    private UserRole recipientRole;

    // Constructor for creating new notifications
    public Notification(String recipientId, String recipientEmail, String recipientPhone,
                        NotificationType type, String title, String content, UserRole recipientRole) {
        this.recipientId = recipientId;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.type = type;
        this.title = title;
        this.content = content;
        this.status = NotificationStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.recipientRole = recipientRole;
    }
}
