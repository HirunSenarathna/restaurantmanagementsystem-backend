package com.sdp.orderservice.exception;

import lombok.Data;

@Data
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);

    }

    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
    }
}
