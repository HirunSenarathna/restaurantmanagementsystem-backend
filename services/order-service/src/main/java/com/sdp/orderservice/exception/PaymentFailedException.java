package com.sdp.orderservice.exception;

import lombok.Getter;

@Getter
public class PaymentFailedException extends RuntimeException {
    private final Long orderId;
    private final String reason;

    public PaymentFailedException(Long orderId, String reason) {
        super("Payment failed for order " + orderId + ": " + reason);
        this.orderId = orderId;
        this.reason = reason;
    }
}
