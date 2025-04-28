package com.sdp.orderservice.kafka;

import com.sdp.orderservice.dto.OrderDTO;
import com.sdp.orderservice.entity.Order;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-updated}")
    private String orderUpdatedTopic;

    public void publishOrderCreatedEvent(OrderDTO orderDTO) {
        try {
            log.info("Publishing order created event: {}", orderDTO.getId());
            kafkaTemplate.send(orderCreatedTopic, orderDTO.getId().toString(), orderDTO);
            log.info("Order created event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish order created event: {}", e.getMessage(), e);
        }
    }

    public void publishOrderStatusUpdatedEvent(Order order) {
        try {
            log.info("Publishing order status updated event: {}, new status: {}",
                    order.getId(), order.getOrderStatus());

            OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(
                    order.getId(),
                    order.getOrderStatus().toString(),
                    order.getCustomerId(),
                    order.getOrderTime(),
                    order.getCompletionTime()
            );

            kafkaTemplate.send(orderUpdatedTopic, order.getId().toString(), event);
            log.info("Order status updated event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish order status updated event: {}", e.getMessage(), e);
        }
    }

    // Inner class representing the event payload
    @Data
    public static class OrderStatusUpdateEvent {
        private Long orderId;
        private String newStatus;
        private Long customerId;
        private String orderTime;
        private String completionTime;

        // No-args constructor for serialization
        public OrderStatusUpdateEvent() {}

        public OrderStatusUpdateEvent(Long orderId, String newStatus, Long customerId,
                                      java.time.LocalDateTime orderTime,
                                      java.time.LocalDateTime completionTime) {
            this.orderId = orderId;
            this.newStatus = newStatus;
            this.customerId = customerId;
            this.orderTime = orderTime != null ? orderTime.toString() : null;
            this.completionTime = completionTime != null ? completionTime.toString() : null;
        }

    }

}
