package com.sdp.orderservice.dto;

import com.sdp.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private Integer tableNumber;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
    private String message;


    // Payment-related fields
    private Long paymentId;
    private String paymentLink;
    private PaymentStatus paymentStatus;
    private boolean isOnline;
}
