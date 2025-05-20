package com.sdp.orderservice.kafka;


import com.sdp.orderservice.dto.PaymentMethod;
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
public class OrderCreatedEvent {

    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private boolean requiresPayment;
    private String returnUrl;  // For online payments
    private LocalDateTime orderTime;
    private String paymentLink;
    private String referenceId;

//    private Long orderId;
//    private Long customerId;
//    private BigDecimal totalAmount;
//    private PaymentMethod paymentMethod;
//    private boolean requiresPayment;
//    private String returnUrl;  // For online payments
//    private LocalDateTime orderTime;
//    private  String paymentLink;


//    private Long orderId;
//    private Long customerId;
//    private BigDecimal totalAmount;
//    private OrderStatus orderStatus;
//    private LocalDateTime orderTime;
//    private PaymentRequest paymentRequest;
}
