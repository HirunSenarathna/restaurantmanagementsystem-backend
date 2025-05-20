package com.sdp.paymentservice.kafka;

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
public class PaymentInitiatedEvent {
    private Long paymentId;
    private Long orderId;
    private Long processedBy;
    private String paymentLink;
    private String transactionId;
    private PaymentStatus status;
    private LocalDateTime timestamp;

    // Optional additional fields
    private Long customerId;
    private String method;
}
