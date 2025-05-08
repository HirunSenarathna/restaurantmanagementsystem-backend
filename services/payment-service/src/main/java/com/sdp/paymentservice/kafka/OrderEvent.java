package com.sdp.paymentservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private Long orderId;
    private Long customerId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
    private String eventType; // ORDER_CREATED, ORDER_UPDATED, ORDER_CANCELLED

}
