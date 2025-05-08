package com.sdp.orderservice.kafka;

import com.sdp.orderservice.dto.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentUpdatedEvent {

    private Long orderId;
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String eventType;
    private LocalDateTime timestamp;
}
