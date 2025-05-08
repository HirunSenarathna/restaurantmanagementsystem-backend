//package com.sdp.paymentservice.kafka;
//
//import com.sdp.paymentservice.exception.PaymentException;
//import com.sdp.paymentservice.external.OrderServiceClient;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentEventConsumer {
//
//    private final OrderServiceClient orderServiceClient;
//
//    @KafkaListener(topics = "${kafka.topics.order-events}", groupId = "${spring.kafka.consumer.group-id}")
//    public void consumeOrderEvent(OrderEvent orderEvent) {
//        log.info("Received order event: {}", orderEvent);
//
//        try {
//            // Handle order events that are relevant to the payment service
//            // For example, if an order is cancelled, we might need to refund the payment
//
//            if ("ORDER_CANCELLED".equals(orderEvent.getEventType())) {
//                log.info("Order cancelled. Order ID: {}. Updating order status in payment service.", orderEvent.getOrderId());
//                // Handle order cancellation logic here
//                // This might trigger a refund process if payment was already made
//            }
//
//        } catch (Exception e) {
//            log.error("Error processing order event: {}", e.getMessage(), e);
//            throw new PaymentException("Failed to process order event: " + e.getMessage());
//        }
//    }
//}
