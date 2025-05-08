package com.sdp.paymentservice.exception;

import lombok.Data;

@Data
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
