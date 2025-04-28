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

public class OrderDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long waiterId;
    private String waiterName;
    private Integer tableNumber;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime completionTime;
    private BigDecimal totalAmount;
    private String specialInstructions;
    private Boolean isPaid;
    private List<OrderItemDTO> items;

}
