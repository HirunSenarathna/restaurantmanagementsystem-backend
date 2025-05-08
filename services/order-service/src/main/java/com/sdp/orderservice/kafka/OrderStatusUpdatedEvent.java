package com.sdp.orderservice.kafka;

import com.sdp.orderservice.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderStatusUpdatedEvent {
    private Long orderId;
    private OrderStatus newStatus;
    private LocalDateTime timestamp;
}
