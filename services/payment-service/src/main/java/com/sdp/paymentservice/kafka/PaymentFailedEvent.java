package com.sdp.paymentservice.kafka;

import com.sdp.paymentservice.model.PaymentMethod;
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
    private LocalDateTime timestamp;

//    private Long paymentId;
//    private Long orderId;
//    private Long customerId;
//    private BigDecimal amount;
//    private PaymentMethod method;
//    private String transactionId;
//    private String errorMessage;
//    private LocalDateTime timestamp;
}
