package com.sdp.analyticsservice.dto;


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
public class OrderDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long waiterId;
    private String waiterName;
    private Integer tableNumber;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private String specialInstructions;
    private Boolean isPaid;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private List<OrderItemDTO> items;
    private String paymentLink;
    private boolean isOnline;

}
