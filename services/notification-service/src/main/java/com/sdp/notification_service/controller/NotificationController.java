package com.sdp.notification_service.controller;

import com.sdp.notification_service.client.UserServiceClient;
import com.sdp.notification_service.model.Notification;
import com.sdp.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserServiceClient userServiceClient;

    @GetMapping("/id/{id}")
    public ResponseEntity<String> sayHello(@PathVariable Long id) {
        var user = userServiceClient.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getFirstname());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadUserNotifications(userId));
    }

    @PutMapping("/{notificationId}/mark-read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@RequestParam String userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadUserNotifications(userId);
        for (Notification notification : unreadNotifications) {
            notificationService.markAsRead(notification.getId());
        }
        return ResponseEntity.ok().build();
    }
}
