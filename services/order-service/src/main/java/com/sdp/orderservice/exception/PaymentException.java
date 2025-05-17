package com.sdp.orderservice.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
