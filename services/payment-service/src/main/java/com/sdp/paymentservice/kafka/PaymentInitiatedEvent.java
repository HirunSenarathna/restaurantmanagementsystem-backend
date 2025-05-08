package com.sdp.paymentservice.kafka;

import com.sdp.paymentservice.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentInitiatedEvent {
    private Long paymentId;
    private Long orderId;
    private String paymentLink;
    private PaymentStatus status;
    private LocalDateTime timestamp;
}
