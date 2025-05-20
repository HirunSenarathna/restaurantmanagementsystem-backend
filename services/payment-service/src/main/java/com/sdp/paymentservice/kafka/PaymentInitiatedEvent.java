package com.sdp.paymentservice.kafka;
import com.sdp.paymentservice.model.PaymentStatus;
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
    private String paymentLink;
    private String transactionId;
    private PaymentStatus status;
    private LocalDateTime timestamp;

    // Optional additional fields
    private Long customerId;
    private String method;
    private Long processedBy;
}
