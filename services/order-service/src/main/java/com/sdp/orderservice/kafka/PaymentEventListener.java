//package com.sdp.orderservice.kafka;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sdp.orderservice.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PaymentEventListener {
//
//    private final OrderService orderService;
//    private final ObjectMapper objectMapper;
//
//    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
//    public void handlePaymentEvent(ConsumerRecord<String, Object> record) {
//        try {
//            JsonNode eventNode = objectMapper.readTree(record.value().toString());
//            String eventType = eventNode.get("eventType").asText();
//
//            log.info("Received payment event of type: {}", eventType);
//
//            if ("PAYMENT_PROCESSED".equals(eventType)) {
//                PaymentProcessedEvent event = objectMapper.convertValue(record.value(), PaymentProcessedEvent.class);
//                orderService.updateOrderPaymentStatus(event);
//                log.info("Order payment status updated for order ID: {}", event.getOrderId());
//            }
//        } catch (Exception e) {
//            log.error("Error processing payment event", e);
//        }
//    }
//}
