package com.sdp.paymentservice.kafka;


import com.sdp.paymentservice.dto.OrderDTO;
import com.sdp.paymentservice.model.PaymentMethod;
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
    private boolean isOnline;
    private OrderDTO orderDTO;
    private String eventType;
    private LocalDateTime timestamp;

//    private Long orderId;
//    private Long customerId;
//    private BigDecimal totalAmount;
//    private OrderStatus orderStatus;
//    private LocalDateTime orderTime;
//    private PaymentRequest paymentRequest;
}
