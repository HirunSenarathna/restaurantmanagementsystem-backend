package com.sdp.orderservice.kafka;

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
public class PaymentProcessedEvent {
    private String paymentId;
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String paymentLink;
    private String eventType;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
