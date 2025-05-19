package com.sdp.paymentservice.kafka;

import com.sdp.orderservice.kafka.OrderCreatedEvent;
import com.sdp.paymentservice.dto.PaymentRequest;
import com.sdp.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "${kafka.topics.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received order created event: {}", event);

        if (event.isPaymentInitiated()) {
            log.info("Payment already initiated for order {}. Skipping payment creation.", event.getOrderId());
            return;
        }

        // Process only if payment is required
        if (event.isRequiresPayment()) {
            try {
                // Create payment request from event data
                PaymentRequest paymentRequest = PaymentRequest.builder()
                        .orderId(event.getOrderId())
                        .processedBy(event.getCustomerId())
                        .amount(event.getTotalAmount())
                        .method(event.getPaymentMethod())
                        .returnUrl(event.getReturnUrl())
                        .isOnline(true)
                        .build();

                // Process payment asynchronously
                paymentService.createPayment(paymentRequest);
            } catch (Exception e) {
                log.error("Error processing payment for order {}: {}",
                        event.getOrderId(), e.getMessage(), e);
                // Handle failure - could publish a payment failed event
            }
        }
    }
}
