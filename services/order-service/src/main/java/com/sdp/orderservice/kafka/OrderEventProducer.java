package com.sdp.orderservice.kafka;

import com.sdp.orderservice.dto.OrderDTO;
import com.sdp.orderservice.dto.PaymentMethod;
import com.sdp.orderservice.dto.PaymentRequest;
import com.sdp.orderservice.dto.PaymentStatus;
import com.sdp.orderservice.entity.Order;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {


//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
//        try {
//            kafkaTemplate.send("order-events", event);
//            log.info("Order created event published successfully for order ID: {}", event.getOrderId());
//        } catch (Exception e) {
//            log.error("Failed to publish order created event for order ID: {}", event.getOrderId(), e);
//        }
//    }
//
//    public void publishOrderNotificationEvent(Long orderId, String message, String notificationType) {
//        try {
//            OrderNotificationEvent event = OrderNotificationEvent.builder()
//                    .orderId(orderId)
//                    .message(message)
//                    .notificationType(notificationType)
//                    .eventType("ORDER_NOTIFICATION")
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            kafkaTemplate.send("order-notifications", event);
//            log.info("Order notification event published successfully for order ID: {}", orderId);
//        } catch (Exception e) {
//            log.error("Failed to publish order notification event for order ID: {}", orderId, e);
//        }
//    }

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    // Existing method
//    public void publishOrderCreatedEvent(OrderDTO orderDTO) {
//        try {
//            // Include payment information in the event
//            OrderCreatedEvent event = OrderCreatedEvent.builder()
//                    .orderId(orderDTO.getId())
//                    .customerId(orderDTO.getCustomerId())
//                    .totalAmount(orderDTO.getTotalAmount())
//                    .paymentMethod(orderDTO.getPaymentMethod())
//                    .isOnline(orderDTO.getPaymentMethod() != PaymentMethod.CASH)
//                    .orderDTO(orderDTO)
//                    .eventType("ORDER_CREATED")
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            kafkaTemplate.send("order-events", event);
//            log.info("Order created event published successfully");
//        } catch (Exception e) {
//            log.error("Failed to publish order created event", e);
//        }
//    }
//
//    // Add method for payment updates
//    public void publishOrderPaymentUpdatedEvent(Long orderId, PaymentStatus paymentStatus,
//                                                String paymentId, String transactionId) {
//        try {
//            OrderPaymentUpdatedEvent event = OrderPaymentUpdatedEvent.builder()
//                    .orderId(orderId)
//                    .paymentId(paymentId)
//                    .paymentStatus(paymentStatus)
//                    .transactionId(transactionId)
//                    .eventType("ORDER_PAYMENT_UPDATED")
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            kafkaTemplate.send("order-events", event);
//            log.info("Order payment updated event published successfully");
//        } catch (Exception e) {
//            log.error("Failed to publish order payment update event", e);
//        }


    //v3

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public void publishOrderCreatedEvent(OrderDTO orderDTO, PaymentRequest paymentRequest) {
//        OrderCreatedEvent event = OrderCreatedEvent.builder()
//                .orderId(orderDTO.getId())
//                .customerId(orderDTO.getCustomerId())
//                .totalAmount(orderDTO.getTotalAmount())
//                .orderStatus(orderDTO.getOrderStatus())
//                .orderTime(orderDTO.getOrderTime())
//                .paymentRequest(paymentRequest)
//                .build();
//
//        log.info("Publishing order created event for order ID: {}", orderDTO.getId());
//        kafkaTemplate.send("order-events", String.valueOf(orderDTO.getId()), event)
//                .addCallback(
//                        result -> log.info("Order created event published successfully"),
//                        ex -> log.error("Failed to publish order created event", ex)
//                );
//    }
//
//    public void publishOrderStatusUpdatedEvent(Order order) {
//        OrderStatusUpdatedEvent event = OrderStatusUpdatedEvent.builder()
//                .orderId(order.getId())
//                .newStatus(order.getOrderStatus())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        log.info("Publishing order status updated event for order ID: {}", order.getId());
//        kafkaTemplate.send("order-events", String.valueOf(order.getId()), event)
//                .addCallback(
//                        result -> log.info("Order status updated event published successfully"),
//                        ex -> log.error("Failed to publish order status updated event", ex)
//                );
//    }

    //v2

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String ORDER_CREATED_TOPIC = "order-created";
    private static final String ORDER_UPDATED_TOPIC = "order-updated";
    private static final String ORDER_NOTIFICATION_TOPIC = "order-notification";

    /**
     * Publishes an event when a new order is created
     *
     * @param orderDTO the created order
     */
    public void publishOrderCreatedEvent(OrderDTO orderDTO) {
        log.info("Publishing order created event for order ID: {}", orderDTO.getId());

        OrderEvent event = OrderEvent.builder()
                .eventType(EventType.ORDER_CREATED)
                .orderId(orderDTO.getId())
                .customerId(orderDTO.getCustomerId())
                .waiterId(orderDTO.getWaiterId())
                .orderStatus(orderDTO.getOrderStatus())
                .totalAmount(orderDTO.getTotalAmount())
                .orderData(orderDTO)
                .build();

        try {
            kafkaTemplate.send(ORDER_CREATED_TOPIC, String.valueOf(orderDTO.getId()), event);
            publishOrderNotification(event);
            log.info("Order created event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish order created event", e);
        }
    }

    /**
     * Publishes an event when an order status is updated
     *
     * @param order the updated order
     */
    public void publishOrderStatusUpdatedEvent(Order order) {
        log.info("Publishing order status updated event for order ID: {}", order.getId());

        OrderEvent event = OrderEvent.builder()
                .eventType(EventType.ORDER_STATUS_UPDATED)
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .waiterId(order.getWaiterId())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .build();

        try {
            kafkaTemplate.send(ORDER_UPDATED_TOPIC, String.valueOf(order.getId()), event);
            publishOrderNotification(event);
            log.info("Order status updated event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish order status updated event", e);
        }
    }

    /**
     * Publishes notification events for other services
     *
     * @param event the order event
     */
    private void publishOrderNotification(OrderEvent event) {
        try {
            kafkaTemplate.send(ORDER_NOTIFICATION_TOPIC, String.valueOf(event.getOrderId()), event);
            log.info("Order notification published successfully");
        } catch (Exception e) {
            log.error("Failed to publish order notification", e);
        }
    }

    //v1

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Value("${kafka.topics.order-created}")
//    private String orderCreatedTopic;
//
//    @Value("${kafka.topics.order-updated}")
//    private String orderUpdatedTopic;
//
//    public void publishOrderCreatedEvent(OrderDTO orderDTO) {
//        try {
//            log.info("Publishing order created event: {}", orderDTO.getId());
//            kafkaTemplate.send(orderCreatedTopic, orderDTO.getId().toString(), orderDTO);
//            log.info("Order created event published successfully");
//        } catch (Exception e) {
//            log.error("Failed to publish order created event: {}", e.getMessage(), e);
//        }
//    }
//
//    public void publishOrderStatusUpdatedEvent(Order order) {
//        try {
//            log.info("Publishing order status updated event: {}, new status: {}",
//                    order.getId(), order.getOrderStatus());
//
//            OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(
//                    order.getId(),
//                    order.getOrderStatus().toString(),
//                    order.getCustomerId(),
//                    order.getOrderTime(),
//                    order.getCompletionTime()
//            );
//
//            kafkaTemplate.send(orderUpdatedTopic, order.getId().toString(), event);
//            log.info("Order status updated event published successfully");
//        } catch (Exception e) {
//            log.error("Failed to publish order status updated event: {}", e.getMessage(), e);
//        }
//    }
//
//    // Inner class representing the event payload
//    @Data
//    public static class OrderStatusUpdateEvent {
//        private Long orderId;
//        private String newStatus;
//        private Long customerId;
//        private String orderTime;
//        private String completionTime;
//
//        // No-args constructor for serialization
//        public OrderStatusUpdateEvent() {}
//
//        public OrderStatusUpdateEvent(Long orderId, String newStatus, Long customerId,
//                                      java.time.LocalDateTime orderTime,
//                                      java.time.LocalDateTime completionTime) {
//            this.orderId = orderId;
//            this.newStatus = newStatus;
//            this.customerId = customerId;
//            this.orderTime = orderTime != null ? orderTime.toString() : null;
//            this.completionTime = completionTime != null ? completionTime.toString() : null;
//        }
//
//    }

}
