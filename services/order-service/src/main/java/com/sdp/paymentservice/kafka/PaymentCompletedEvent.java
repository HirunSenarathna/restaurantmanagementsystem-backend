package com.sdp.paymentservice.kafka;

import com.sdp.orderservice.dto.PaymentMethod;
import com.sdp.orderservice.dto.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private Long paymentId;
    private Long orderId;
    private Long customerId;
    private Long processedBy;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime timestamp;
}
