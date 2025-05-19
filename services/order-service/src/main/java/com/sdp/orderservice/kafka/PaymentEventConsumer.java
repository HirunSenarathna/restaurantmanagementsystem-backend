package com.sdp.orderservice.kafka;

import com.sdp.orderservice.service.OrderService;
import com.sdp.paymentservice.kafka.PaymentCompletedEvent;
import com.sdp.paymentservice.kafka.PaymentFailedEvent;
import com.sdp.paymentservice.kafka.PaymentInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = "${kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Received payment completed event: {}", event);

        try {
            // Update order status to paid
            orderService.updateOrderPaymentStatus(
                    event.getOrderId(),
                    true,
                    event.getMethod(),
                    event.getTransactionId(),
                    event.getPaymentStatus()
            );
        } catch (Exception e) {
            log.error("Error updating order payment status: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        log.info("Received payment failed event: {}", event);

        try {
            // Update order with payment failure info
            orderService.recordPaymentFailure(
                    event.getOrderId(),
                    event.getErrorMessage()
            );
        } catch (Exception e) {
            log.error("Error recording payment failure: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.payment-initiated}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentInitiatedEvent(PaymentInitiatedEvent event) {
        log.info("Received payment initiated event: {}", event);

        try {
            // Update order with payment link
            orderService.updateOrderWithPaymentLink(
                    event.getOrderId(),
                    event.getPaymentId(),
                    event.getPaymentLink()
            );
        } catch (Exception e) {
            log.error("Error updating order with payment link: {}", e.getMessage(), e);
        }
    }
}
