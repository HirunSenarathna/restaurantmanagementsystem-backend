package com.sdp.orderservice.exception;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class OrderPaymentException extends RuntimeException {
    public OrderPaymentException(String message) {
        super(message);
    }
}
