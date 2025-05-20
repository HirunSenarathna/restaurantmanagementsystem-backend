package com.sdp.paymentservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRefundedEvent {

    private Long paymentId;
    private Long orderId;
    private Long customerId;
    private Long processedBy;
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private String reason;
    private LocalDateTime timestamp;

//    private Long paymentId;
//    private Long orderId;
//    private Long customerId;
//    private BigDecimal amount;
//    private BigDecimal refundAmount;
//    private String reason;
//    private LocalDateTime timestamp;
}
