package com.sdp.paymentservice.dto;

import com.sdp.paymentservice.model.PaymentMethod;
import com.sdp.paymentservice.model.PaymentStatus;
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
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
//    private Long customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private String transactionId;
    private long processedBy;
    private  String clientSecret;
    private  boolean isOnline;
    private String paymentLink;
    private String receiptUrl;
    private BigDecimal refundAmount;
    private String refundReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
//
//    private Long paymentId;
//    private Long orderId;
//    private Long customerId;
////    private String method;
//    private BigDecimal amount;
//    private PaymentStatus status;
//    private String transactionId;
//    private LocalDateTime timestamp;
//    private String message;
}
