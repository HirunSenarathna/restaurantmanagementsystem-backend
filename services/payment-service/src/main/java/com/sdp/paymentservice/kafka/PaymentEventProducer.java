package com.sdp.paymentservice.kafka;

import com.sdp.paymentservice.model.Payment;
import com.sdp.paymentservice.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public void publishPaymentProcessedEvent(Payment payment) {
//        try {
//            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
//                    .paymentId(payment.getId().toString())
//                    .orderId(payment.getOrderId())
//                    .customerId(payment.getCustomerId())
//                    .amount(payment.getAmount())
//                    .paymentStatus(payment.getStatus())
//                    .transactionId(payment.getTransactionId())
//                    .paymentLink(payment.getPaymentLink())
//                    .eventType("PAYMENT_PROCESSED")
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            kafkaTemplate.send("payment-events", event);
//            log.info("Payment processed event published successfully for payment ID: {}", payment.getId());
//        } catch (Exception e) {
//            log.error("Failed to publish payment processed event for payment ID: {}", payment.getId(), e);
//        }
//    }

//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public void publishPaymentInitiatedEvent(PaymentInitiatedEvent event) {
//        log.info("Publishing payment initiated event for payment ID: {}", event.getPaymentId());
//        kafkaTemplate.send("payment-status", String.valueOf(event.getPaymentId()), event)
//                .addCallback(
//                        result -> log.info("Payment initiated event published successfully"),
//                        ex -> log.error("Failed to publish payment initiated event", ex)
//                );
//    }
//
//    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
//        log.info("Publishing payment completed event for payment ID: {}", event.getPaymentId());
//        kafkaTemplate.send("payment-status", String.valueOf(event.getPaymentId()), event)
//                .addCallback(
//                        result -> log.info("Payment completed event published successfully"),
//                        ex -> log.error("Failed to publish payment completed event", ex)
//                );
//    }
//
//    public void publishPaymentFailedEvent(PaymentFailedEvent event) {
//        log.info("Publishing payment failed event for order ID: {}", event.getOrderId());
//        kafkaTemplate.send("payment-failed-dlq", String.valueOf(event.getOrderId()), event)
//                .addCallback(
//                        result -> log.info("Payment failed event published successfully"),
//                        ex -> log.error("Failed to publish payment failed event", ex)
//                );
//    }
//
//    public void publishPaymentRefundedEvent(PaymentRefundedEvent event) {
//        log.info("Publishing payment refunded event for payment ID: {}", event.getPaymentId());
//        kafkaTemplate.send("payment-status", String.valueOf(event.getPaymentId()), event)
//                .addCallback(
//                        result -> log.info("Payment refunded event published successfully"),
//                        ex -> log.error("Failed to publish payment refunded event", ex)
//                );
//    }
//
//    public void publishPaymentCancelledEvent(PaymentCancelledEvent event) {
//        log.info("Publishing payment cancelled event for payment ID: {}", event.getPaymentId());
//        kafkaTemplate.send("payment-status", String.valueOf(event.getPaymentId()), event)
//                .addCallback(
//                        result -> log.info("Payment cancelled event published successfully"),
//                        ex -> log.error("Failed to publish payment cancelled event", ex)
//                );
//    }

    //v2

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_COMPLETED_TOPIC = "payment-completed-events";
    private static final String PAYMENT_FAILED_TOPIC = "payment-failed-events";
    private static final String PAYMENT_REFUNDED_TOPIC = "payment-refunded-events";
    private static final String PAYMENT_CANCELLED_TOPIC = "payment-cancelled-events";

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Publishing payment completed event: {}", event);
        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event.getOrderId().toString(), event);
    }

    public void publishPaymentFailedEvent(PaymentFailedEvent event) {
        log.info("Publishing payment failed event: {}", event);
        kafkaTemplate.send(PAYMENT_FAILED_TOPIC, event.getOrderId().toString(), event);
    }

    public void publishPaymentRefundedEvent(PaymentRefundedEvent event) {
        log.info("Publishing payment refunded event: {}", event);
        kafkaTemplate.send(PAYMENT_REFUNDED_TOPIC, event.getOrderId().toString(), event);
    }

    public void publishPaymentCancelledEvent(PaymentCancelledEvent event) {
        log.info("Publishing payment cancelled event: {}", event);
        kafkaTemplate.send(PAYMENT_CANCELLED_TOPIC, event.getOrderId().toString(), event);
    }

    //v1

//    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
//
//    @Value("${kafka.topics.payment-events}")
//    private String paymentEventsTopic;
//
//    public void sendPaymentEvent(Payment payment) {
//        String eventType = determineEventType(payment);
//
//        PaymentEvent paymentEvent = PaymentEvent.builder()
//                .paymentId(payment.getId())
//                .orderId(payment.getOrderId())
//                .customerId(payment.getCustomerId())
//                .amount(payment.getAmount())
//                .status(payment.getStatus())
//                .method(payment.getMethod())
//                .transactionId(payment.getTransactionId())
//                .timestamp(payment.getUpdatedAt() != null ? payment.getUpdatedAt() : payment.getCreatedAt())
//                .eventType(eventType)
//                .build();
//
//        String key = payment.getOrderId().toString();
//
//        CompletableFuture<SendResult<String, PaymentEvent>> future =
//                kafkaTemplate.send(paymentEventsTopic, key, paymentEvent);
//
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                log.info("Payment event sent successfully. Event type: {}, Payment ID: {}, Order ID: {}, Offset: {}",
//                        eventType, payment.getId(), payment.getOrderId(), result.getRecordMetadata().offset());
//            } else {
//                log.error("Failed to send payment event. Event type: {}, Payment ID: {}, Order ID: {}",
//                        eventType, payment.getId(), payment.getOrderId(), ex);
//            }
//        });
//    }
//
//    private String determineEventType(Payment payment) {
//        if (payment.getUpdatedAt() == null) {
//            return "PAYMENT_CREATED";
//        } else if (payment.getStatus() == PaymentStatus.REFUNDED) {
//            return "PAYMENT_REFUNDED";
//        } else {
//            return "PAYMENT_UPDATED";
//        }
//    }

}
