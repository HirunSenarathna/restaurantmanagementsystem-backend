package com.sdp.notification_service.exception;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
