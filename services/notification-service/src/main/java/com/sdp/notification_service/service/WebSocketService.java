package com.sdp.notification_service.service;

import com.sdp.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Notification notification) {
        String destination = "/topic/user." + notification.getRecipientId();
        messagingTemplate.convertAndSend(destination, notification);
        log.info("WebSocket notification sent to: {}", destination);
    }

    public void broadcastToRole(Notification notification, String role) {
        String destination = "/topic/role." + role;
        messagingTemplate.convertAndSend(destination, notification);
        log.info("WebSocket notification broadcast to role: {}", role);
    }
}
