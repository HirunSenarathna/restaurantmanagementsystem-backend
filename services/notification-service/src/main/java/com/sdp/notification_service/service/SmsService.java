package com.sdp.notification_service.service;

import com.sdp.notification_service.model.Notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
@Slf4j
public class SmsService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phone.number}")
    private String FROM_PHONE_NUMBER;

    public void sendSms(Notification notification) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                            new PhoneNumber(notification.getRecipientPhone()),
                            new PhoneNumber(FROM_PHONE_NUMBER),
                            notification.getContent())
                    .create();

            log.info("SMS sent with SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
        }
    }
}
