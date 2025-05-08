//package com.sdp.orderservice.kafka;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.sdp.orderservice.service.OrderService;
//import lombok.*;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//
//@Component
//@RequiredArgsConstructor
//public class OrderEventListener {
//
//    private final OrderService orderService;
//
//    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
//    public void handlePaymentEvent(ConsumerRecord<String, Object> record) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.registerModule(new JavaTimeModule());
//
//            JsonNode eventNode = mapper.readTree(record.value().toString());
//            String eventType = eventNode.get("eventType").asText();
//
//            if ("PAYMENT_PROCESSED".equals(eventType)) {
//                PaymentProcessedEvent event = mapper.convertValue(record.value(), PaymentProcessedEvent.class);
//                orderService.updateOrderPaymentStatus(event.getOrderId(),
//                        event.getPaymentStatus(),
//                        event.getPaymentId());
//            }
//        } catch (Exception e) {
//            log.error("Error processing payment event", e);
//        }
//    }
//}
