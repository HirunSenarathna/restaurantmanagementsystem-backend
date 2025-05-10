package com.sdp.paymentservice.kafka;


import com.sdp.orderservice.dto.PaymentMethod;
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
public class PaymentFailedEvent {

    private Long paymentId;
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String transactionId;
    private String errorMessage;
    private String errorCode;
    private LocalDateTime timestamp;

    // Optional additional fields
    private Integer failureAttempt;   // Which attempt number this is
    private Boolean retriable;         // Whether this failure can be retried
    private LocalDateTime retryAfter;  // When to retry if applicable
    private String gatewayResponse;    // Raw response from payment gateway

//    private Long paymentId;
//    private Long orderId;
//    private Long customerId;
//    private BigDecimal amount;
//    private PaymentMethod method;
//    private String transactionId;
//    private String errorMessage;
//    private LocalDateTime timestamp;
}
