package com.sdp.orderservice.dto;

import com.sdp.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "New status is required")
    private OrderStatus newStatus;

    //private String notes;
}
