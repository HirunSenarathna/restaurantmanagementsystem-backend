package com.sdp.paymentservice.dto;

import com.sdp.paymentservice.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryDTO {

    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private OrderStatus status;
}
