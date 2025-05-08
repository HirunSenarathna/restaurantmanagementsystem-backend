package com.sdp.orderservice.dto;

import com.sdp.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private OrderStatus status;
}
