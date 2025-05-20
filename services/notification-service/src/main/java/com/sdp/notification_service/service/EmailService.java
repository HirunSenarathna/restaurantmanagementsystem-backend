package com.sdp.notification_service.service;

import com.sdp.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipientEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());

            mailSender.send(message);
            log.info("Email sent to: {}", notification.getRecipientEmail());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
}
