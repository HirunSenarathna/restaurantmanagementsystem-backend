//package com.sdp.paymentservice.kafka;
//
//import com.sdp.paymentservice.dto.PaymentResponse;
//import com.sdp.paymentservice.service.PaymentService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class OrderEventConsumer {
//    private final PaymentService paymentService;
//    private final PaymentEventProducer paymentEventProducer;
//
//    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
//    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent event, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
//        log.info("Received OrderCreatedEvent for order ID: {}", event.getOrderId());
//
//        if (event.getPaymentRequest() != null) {
//            try {
//                // Ensure idempotency: check if payment already exists
//                if (paymentService.paymentExists(event.getPaymentRequest().getOrderId())) {
//                    log.info("Payment already initiated for order ID: {}", event.getOrderId());
//                    return;
//                }
//                PaymentResponse paymentResponse = paymentService.createPayment(event.getPaymentRequest());
//                log.info("Payment initiated for order ID: {}", event.getOrderId());
//
//                // Publish PaymentInitiatedEvent
//                paymentEventProducer.publishPaymentInitiatedEvent(
//                        PaymentInitiatedEvent.builder()
//                                .paymentId(paymentResponse.getPaymentId())
//                                .orderId(paymentResponse.getOrderId())
//                                .paymentLink(paymentResponse.getPaymentLink())
//                                .status(paymentResponse.getStatus())
//                                .timestamp(LocalDateTime.now())
//                                .build()
//                );
//            } catch (Exception e) {
//                log.error("Failed to initiate payment for order ID: {}. Error: {}", event.getOrderId(), e.getMessage());
//                // Publish to DLQ
//                paymentEventProducer.publishPaymentFailedEvent(
//                        PaymentFailedEvent.builder()
//                                .orderId(event.getOrderId())
//                                .customerId(event.getPaymentRequest().getCustomerId())
//                                .amount(event.getPaymentRequest().getAmount())
//                                .method(event.getPaymentRequest().getMethod())
//                                .errorMessage(e.getMessage())
//                                .timestamp(LocalDateTime.now())
//                                .build()
//                );
//            }
//        } else {
//            log.info("No payment required for order ID: {} (likely CASH payment)", event.getOrderId());
//        }
//    }
//}
