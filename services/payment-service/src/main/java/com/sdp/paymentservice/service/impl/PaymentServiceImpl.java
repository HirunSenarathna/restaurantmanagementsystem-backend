package com.sdp.paymentservice.service.impl;

import com.sdp.paymentservice.dto.*;
import com.sdp.paymentservice.exception.PaymentException;
import com.sdp.paymentservice.exception.PaymentNotFoundException;
import com.sdp.paymentservice.exception.ResourceNotFoundException;
import com.sdp.paymentservice.external.OrderServiceClient;
import com.sdp.paymentservice.kafka.*;
import com.sdp.paymentservice.model.*;
import com.sdp.paymentservice.repository.PaymentRepository;
import com.sdp.paymentservice.service.PaymentGatewayService;
import com.sdp.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final PaymentEventProducer paymentEventProducer;
    private final PaymentGatewayService paymentGateway;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        log.info("Creating payment for order: {}", paymentRequest.getOrderId());

        try {
            // Create payment entity directly from request
            // We no longer need to validate the order exists via synchronous call
            Payment payment = Payment.builder()
                    .orderId(paymentRequest.getOrderId())
                    .customerId(paymentRequest.getCustomerId())
                    .amount(paymentRequest.getAmount())
                    .status(PaymentStatus.PENDING)
                    .method(paymentRequest.getMethod())
                    .createdAt(LocalDateTime.now())
                    .build();

            // Save initial payment record
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment record created: {}", savedPayment);

            try {
                // If it's an online payment, generate payment link
                if (paymentRequest.isOnline()) {
                    // Generate payment link through payment gateway
                    Map<String, String> gatewayResponse = paymentGateway.initiatePayment(
                            savedPayment.getId().toString(),
                            paymentRequest.getAmount(),
                            "Payment for Order #" + paymentRequest.getOrderId(),
                            paymentRequest.getReturnUrl()
                    );

                    // Update payment with gateway response
                    savedPayment.setTransactionId(gatewayResponse.get("transactionId"));
                    savedPayment.setPaymentLink(gatewayResponse.get("paymentLink"));
                    savedPayment.setPaymentGatewayResponse(gatewayResponse.toString());
                    savedPayment = paymentRepository.save(savedPayment);

                    // Publish event about payment link creation
                    publishPaymentInitiatedEvent(savedPayment);

                    // Return response with payment link
                    return PaymentResponse.builder()
                            .paymentId(savedPayment.getId())
                            .orderId(savedPayment.getOrderId())
                            .amount(savedPayment.getAmount())
                            .status(savedPayment.getStatus())
                            .paymentLink(savedPayment.getPaymentLink())
                            .transactionId(savedPayment.getTransactionId())
                            .message("Payment initiated successfully")
                            .build();
                } else {
                    // For in-person payments, process immediately
                    return processPayment(paymentRequest);
                }
            } catch (Exception e) {
                log.error("Error during payment creation: {}", e.getMessage());
                // Update payment status to FAILED
                savedPayment.setStatus(PaymentStatus.FAILED);
                savedPayment.setPaymentGatewayResponse("Error: " + e.getMessage());
                savedPayment = paymentRepository.save(savedPayment);

                // Publish payment failed event
                publishPaymentFailedEvent(savedPayment, e.getMessage());

                throw new PaymentException("Payment initiation failed: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error creating payment record: {}", e.getMessage());
            throw new PaymentException("Payment creation failed: " + e.getMessage());
        }


//        log.info("Creating payment for order: {}", paymentRequest.getOrderId());
//
//        try {
//
//            log.info("in try block");
//            // Validate the order exists and get details
//            OrderDTO orderDTO = orderServiceClient.getOrderById(paymentRequest.getOrderId());
//
//            log.info("Order details: {}", orderDTO);
//
//            // Create payment entity
//            Payment payment = Payment.builder()
//                    .orderId(paymentRequest.getOrderId())
//                    .customerId(paymentRequest.getCustomerId())
//                    .amount(paymentRequest.getAmount())
//                    .status(PaymentStatus.PENDING)
//                    .method(paymentRequest.getMethod())
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            // Save initial payment record
//            Payment savedPayment = paymentRepository.save(payment);
//            log.info("Payment record created: {}", savedPayment);
//
//            try {
//                // If it's an online payment, generate payment link
//                if (paymentRequest.isOnline()) {
//                    // Generate payment link through payment gateway
//                    Map<String, String> gatewayResponse = paymentGateway.initiatePayment(
//                            savedPayment.getId().toString(),
//                            paymentRequest.getAmount(),
//                            "Payment for Order #" + paymentRequest.getOrderId(),
//                            paymentRequest.getReturnUrl()
//                    );
//
//                    // Update payment with gateway response
//                    savedPayment.setTransactionId(gatewayResponse.get("transactionId"));
//                    savedPayment.setPaymentLink(gatewayResponse.get("paymentLink"));
//                    savedPayment.setPaymentGatewayResponse(gatewayResponse.toString());
//                    savedPayment = paymentRepository.save(savedPayment);
//
//                    // Return response with payment link
//                    return PaymentResponse.builder()
//                            .paymentId(savedPayment.getId())
//                            .orderId(savedPayment.getOrderId())
//                            .amount(savedPayment.getAmount())
//                            .status(savedPayment.getStatus())
//                            .paymentLink(savedPayment.getPaymentLink())
//                            .transactionId(savedPayment.getTransactionId())
//                            .message("Payment initiated successfully")
//                            .build();
//                } else {
//                    // For in-person payments, process immediately
//                    return processPayment(paymentRequest);
//                }
//            } catch (Exception e) {
//                log.error("Error during payment creation: {}", e.getMessage());
//                // Update payment status to FAILED
//                savedPayment.setStatus(PaymentStatus.FAILED);
//                savedPayment.setPaymentGatewayResponse("Error: " + e.getMessage());
//                paymentRepository.save(savedPayment);
//
//                throw new PaymentException("Payment initiation failed: " + e.getMessage());
//            }
//        } catch (Exception e) {
//            throw new ResourceNotFoundException("Order not found with ID: " + paymentRequest.getOrderId());
//        }

    }

    // Helper methods to publish events
    private void publishPaymentInitiatedEvent(Payment payment) {
        PaymentInitiatedEvent event = PaymentInitiatedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .paymentLink(payment.getPaymentLink())
                .status(payment.getStatus())
                .timestamp(LocalDateTime.now())
                .build();

        paymentEventProducer.publishPaymentInitiatedEvent(event);
    }

    private void publishPaymentFailedEvent(Payment payment, String errorMessage) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .transactionId(payment.getTransactionId())
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();

        paymentEventProducer.publishPaymentFailedEvent(event);
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Processing in-person payment for order: {}", paymentRequest.getOrderId());

        // For in-person payments like card payments at the restaurant
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .customerId(paymentRequest.getCustomerId())
                .amount(paymentRequest.getAmount())
                .status(PaymentStatus.PROCESSING)
                .method(paymentRequest.getMethod())
                .createdAt(LocalDateTime.now())
                .build();

        // Save initial payment record
        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Process the payment through payment gateway
            // For cash, we don't need to call the gateway
            if (paymentRequest.getMethod() != PaymentMethod.CASH) {
                Map<String, String> gatewayResponse = paymentGateway.processPayment(
                        savedPayment.getId().toString(),
                        paymentRequest.getAmount()
                );

                savedPayment.setTransactionId(gatewayResponse.get("transactionId"));
                savedPayment.setReceiptUrl(gatewayResponse.get("receiptUrl"));
                savedPayment.setPaymentGatewayResponse(gatewayResponse.toString());
            } else {
                // For cash payments, generate a simple transaction ID
                savedPayment.setTransactionId("CASH-" + System.currentTimeMillis());
            }

            // Mark payment as completed
            savedPayment.setStatus(PaymentStatus.COMPLETED);
            savedPayment = paymentRepository.save(savedPayment);

            // Notify order service about payment completion
            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .paymentId(savedPayment.getId())
                    .orderId(savedPayment.getOrderId())
                    .customerId(savedPayment.getCustomerId())
                    .amount(savedPayment.getAmount())
                    .method(savedPayment.getMethod())
                    .transactionId(savedPayment.getTransactionId())
                    .timestamp(LocalDateTime.now())
                    .build();

            paymentEventProducer.publishPaymentCompletedEvent(event);

            return PaymentResponse.builder()
                    .paymentId(savedPayment.getId())
                    .orderId(savedPayment.getOrderId())
                    .amount(savedPayment.getAmount())
                    .status(savedPayment.getStatus())
                    .transactionId(savedPayment.getTransactionId())
                    .receiptUrl(savedPayment.getReceiptUrl())
                    .message("Payment processed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage());
            // Update payment status to FAILED
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setPaymentGatewayResponse("Error: " + e.getMessage());
            paymentRepository.save(savedPayment);

            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse handlePaymentCallback(String transactionId, String status, Map<String, String> gatewayParams) {
        log.info("Payment gateway callback received for transaction: {}", transactionId);

        // Find payment by transaction ID
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for transaction: " + transactionId));

        // Update payment status based on gateway response
        PaymentStatus newStatus;

        if ("success".equalsIgnoreCase(status)) {
            newStatus = PaymentStatus.COMPLETED;
        } else if ("failed".equalsIgnoreCase(status)) {
            newStatus = PaymentStatus.FAILED;
        } else {
            newStatus = PaymentStatus.PROCESSING;
        }

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setPaymentGatewayResponse(gatewayParams.toString());

        // If receipt URL is provided, update it
        if (gatewayParams.containsKey("receiptUrl")) {
            payment.setReceiptUrl(gatewayParams.get("receiptUrl"));
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // If payment is successful, notify order service
        if (newStatus == PaymentStatus.COMPLETED) {
            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .paymentId(updatedPayment.getId())
                    .orderId(updatedPayment.getOrderId())
                    .customerId(updatedPayment.getCustomerId())
                    .amount(updatedPayment.getAmount())
                    .method(updatedPayment.getMethod())
                    .transactionId(updatedPayment.getTransactionId())
                    .timestamp(LocalDateTime.now())
                    .build();

            paymentEventProducer.publishPaymentCompletedEvent(event);
        } else if (newStatus == PaymentStatus.FAILED) {
            // Notify about failed payment
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .paymentId(updatedPayment.getId())
                    .orderId(updatedPayment.getOrderId())
                    .customerId(updatedPayment.getCustomerId())
                    .amount(updatedPayment.getAmount())
                    .method(updatedPayment.getMethod())
                    .transactionId(updatedPayment.getTransactionId())
                    .errorMessage("Payment failed: " + gatewayParams.getOrDefault("errorMessage", "Unknown error"))
                    .timestamp(LocalDateTime.now())
                    .build();

            paymentEventProducer.publishPaymentFailedEvent(event);
        }

        return PaymentResponse.builder()
                .paymentId(updatedPayment.getId())
                .orderId(updatedPayment.getOrderId())
                .amount(updatedPayment.getAmount())
                .status(updatedPayment.getStatus())
                .transactionId(updatedPayment.getTransactionId())
                .receiptUrl(updatedPayment.getReceiptUrl())
                .message("Payment status updated to " + updatedPayment.getStatus())
                .build();
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .transactionId(payment.getTransactionId())
                .paymentLink(payment.getPaymentLink())
                .receiptUrl(payment.getReceiptUrl())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        Optional<Payment> payments = paymentRepository.findByOrderId(orderId);

        return payments.stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .customerId(payment.getCustomerId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .method(payment.getMethod())
                        .transactionId(payment.getTransactionId())
                        .paymentLink(payment.getPaymentLink())
                        .receiptUrl(payment.getReceiptUrl())
                        .createdAt(payment.getCreatedAt())
                        .updatedAt(payment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomerId(Long customerId) {
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);

        return payments.stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .customerId(payment.getCustomerId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .method(payment.getMethod())
                        .transactionId(payment.getTransactionId())
                        .paymentLink(payment.getPaymentLink())
                        .receiptUrl(payment.getReceiptUrl())
                        .createdAt(payment.getCreatedAt())
                        .updatedAt(payment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId, RefundRequest refundRequest) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        // Only completed payments can be refunded
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Payment cannot be refunded. Current status: " + payment.getStatus());
        }

        try {
            // For cash payments, just mark as refunded
            if (payment.getMethod() == PaymentMethod.CASH) {
                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setRefundAmount(refundRequest.getAmount());
                payment.setRefundReason(refundRequest.getReason());
                payment.setUpdatedAt(LocalDateTime.now());
            } else {
                // Process refund through payment gateway
                Map<String, String> refundResponse = paymentGateway.processRefund(
                        payment.getTransactionId(),
                        refundRequest.getAmount(),
                        refundRequest.getReason()
                );

                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setRefundAmount(refundRequest.getAmount());
                payment.setRefundReason(refundRequest.getReason());
                payment.setRefundTransactionId(refundResponse.get("refundTransactionId"));
                payment.setRefundResponse(refundResponse.toString());
                payment.setUpdatedAt(LocalDateTime.now());
            }

            Payment refundedPayment = paymentRepository.save(payment);

            // Notify order service about refund
            PaymentRefundedEvent event = PaymentRefundedEvent.builder()
                    .paymentId(refundedPayment.getId())
                    .orderId(refundedPayment.getOrderId())
                    .customerId(refundedPayment.getCustomerId())
                    .amount(refundedPayment.getAmount())
                    .refundAmount(refundedPayment.getRefundAmount())
                    .reason(refundedPayment.getRefundReason())
                    .timestamp(LocalDateTime.now())
                    .build();

//            paymentEventProducer.publishPaymentRefundedEvent(event);

            return PaymentResponse.builder()
                    .paymentId(refundedPayment.getId())
                    .orderId(refundedPayment.getOrderId())
                    .amount(refundedPayment.getAmount())
                    .status(refundedPayment.getStatus())
                    .refundAmount(refundedPayment.getRefundAmount())
                    .refundReason(refundedPayment.getRefundReason())
                    .message("Payment refunded successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error during payment refund: {}", e.getMessage());
            throw new PaymentException("Payment refund failed: " + e.getMessage());
        }
    }

    @Override
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);

        List<PaymentResponse> paymentResponses = payments.getContent().stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .customerId(payment.getCustomerId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .method(payment.getMethod())
                        .transactionId(payment.getTransactionId())
                        .createdAt(payment.getCreatedAt())
                        .updatedAt(payment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(paymentResponses, pageable, payments.getTotalElements());
    }

    @Override
    public PaymentSummaryResponse getPaymentSummary(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Payment> completedPayments = paymentRepository.findByStatusAndCreatedAtBetween(
                PaymentStatus.COMPLETED, startOfDay, endOfDay);

        BigDecimal totalAmount = completedPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<PaymentMethod, Long> paymentsByMethod = completedPayments.stream()
                .collect(Collectors.groupingBy(Payment::getMethod, Collectors.counting()));

        return PaymentSummaryResponse.builder()
                .date(date)
                .totalAmount(totalAmount)
                .totalTransactions(completedPayments.size())
                .paymentsByMethod(paymentsByMethod)
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        // Only pending or processing payments can be canceled
        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new PaymentException("Payment cannot be canceled. Current status: " + payment.getStatus());
        }

        try {
            // If there's a transaction ID, cancel it in the payment gateway
            if (payment.getTransactionId() != null && payment.getMethod() != PaymentMethod.CASH) {
                paymentGateway.cancelPayment(payment.getTransactionId());
            }

            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setUpdatedAt(LocalDateTime.now());

            Payment cancelledPayment = paymentRepository.save(payment);

            // Notify order service about cancellation
            PaymentCancelledEvent event = PaymentCancelledEvent.builder()
                    .paymentId(cancelledPayment.getId())
                    .orderId(cancelledPayment.getOrderId())
                    .customerId(cancelledPayment.getCustomerId())
                    .timestamp(LocalDateTime.now())
                    .build();

//            paymentEventProducer.publishPaymentCancelledEvent(event);

            return PaymentResponse.builder()
                    .paymentId(cancelledPayment.getId())
                    .orderId(cancelledPayment.getOrderId())
                    .amount(cancelledPayment.getAmount())
                    .status(cancelledPayment.getStatus())
                    .message("Payment cancelled successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error during payment cancellation: {}", e.getMessage());
            throw new PaymentException("Payment cancellation failed: " + e.getMessage());
        }
    }


}
