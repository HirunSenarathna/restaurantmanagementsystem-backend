package com.sdp.orderservice.kafka;

import com.sdp.orderservice.dto.OrderDTO;
import com.sdp.orderservice.entity.OrderStatus;
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
public class OrderEvent {

    private EventType eventType;
    private Long orderId;
    private Long customerId;
    private Long waiterId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
    private OrderDTO orderData;

    // Set timestamp to current time when object is created
    @Builder.Default
    private LocalDateTime eventTimestamp = LocalDateTime.now();
}
