package com.sdp.paymentservice.service.impl;

import com.sdp.paymentservice.dto.PaymentRequest;
import com.sdp.paymentservice.exception.PaymentException;
import com.sdp.paymentservice.service.PaymentGatewayService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class StripePaymentGatewayService implements PaymentGatewayService {

    @Value("${payment.gateway.stripe.api-key}")
    private String apiKey;

    @Value("${payment.gateway.stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        log.info("Stripe API Key: {}", apiKey);
        Stripe.apiKey = apiKey;
        log.info("Stripe payment gateway initialized");
    }

    @Override
    public Map<String, String> initiatePayment(String referenceId, BigDecimal amount, String description, String returnUrl) {
        log.info("Initiating payment through Stripe: {}", referenceId);
        try {
            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();


            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(returnUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(returnUrl + "?canceled=true")
                    .setClientReferenceId(referenceId)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("lkr")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(description)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("transactionId", session.getId());
            response.put("paymentLink", session.getUrl());
            response.put("status", "pending");


            log.info("Payment initiated with transaction ID: {}", session.getId());
            return response;

        } catch (StripeException e) {
            log.error("Error initiating payment: {}", e.getMessage());
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> processPayment(String referenceId, BigDecimal amount) {
        log.info("Processing payment through Stripe: {}", referenceId);
        try {
            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();


            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setCurrency("lkr")
                    .setAmount(amountInCents)
                    .setDescription("Payment for reference: " + referenceId)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("transactionId", paymentIntent.getId());
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("status", paymentIntent.getStatus());

            log.info("Payment processed with transaction ID: {}", paymentIntent.getId());
            return response;

        } catch (StripeException e) {
            log.error("Error processing payment: {}, request-id: {}", e.getMessage(), e.getRequestId());
            throw new PaymentException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> processRefund(String transactionId, BigDecimal amount, String reason) {
        log.info("Processing refund for transaction: {}", transactionId);
        try {
            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(transactionId)
                    .setAmount(amountInCents)
                    .setReason(convertToStripeRefundReason(reason))
                    .build();

            Refund refund = Refund.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("refundTransactionId", refund.getId());
            response.put("status", refund.getStatus());
            response.put("originalTransactionId", transactionId);

            log.info("Refund processed with transaction ID: {}", refund.getId());
            return response;

        } catch (StripeException e) {
            log.error("Error processing refund: {}", e.getMessage());
            throw new PaymentException("Failed to process refund: " + e.getMessage());
        }
    }

    @Override
    public void cancelPayment(String transactionId) {
        log.info("Cancelling payment for transaction: {}", transactionId);
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(transactionId);
            if (paymentIntent.getStatus().equals("requires_payment_method") ||
                    paymentIntent.getStatus().equals("requires_confirmation") ||
                    paymentIntent.getStatus().equals("requires_action")) {

                PaymentIntent canceledIntent = paymentIntent.cancel();
                log.info("Payment cancelled for transaction ID: {}", canceledIntent.getId());
            } else {
                log.warn("Payment cannot be cancelled, status: {}", paymentIntent.getStatus());
                throw new PaymentException("Cannot cancel payment with status: " + paymentIntent.getStatus());
            }
        } catch (StripeException e) {
            log.error("Error cancelling payment: {}", e.getMessage());
            throw new PaymentException("Failed to cancel payment: " + e.getMessage());
        }
    }

    private RefundCreateParams.Reason convertToStripeRefundReason(String reason) {
        if (reason == null) {
            return RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
        }

        reason = reason.toLowerCase();
        if (reason.contains("duplicate")) {
            return RefundCreateParams.Reason.DUPLICATE;
        } else if (reason.contains("fraud")) {
            return RefundCreateParams.Reason.FRAUDULENT;
        } else {
            return RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
        }
    }

//    @Value("${payment.gateway.stripe.api-key}")
//    private String apiKey;
//
//    @Value("${payment.gateway.stripe.webhook-secret}")
//    private String webhookSecret;
//
//    @PostConstruct
//    public void init() {
//        log.info("Stripe API Key: {}", apiKey);
//        // Initialize Stripe with your API key
//        Stripe.apiKey = apiKey;
//        log.info("Stripe payment gateway initialized");
//    }
//
//    @Override
//    public Map<String, String> initiatePayment(String referenceId, BigDecimal amount, String description, String returnUrl) {
//        log.info("Initiating payment through Stripe: {}", referenceId);
//        try {
//            // Convert amount to cents (Stripe uses the smallest currency unit)
//            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
//
//            // Create a Checkout Session for redirect-based payments
//            SessionCreateParams params = SessionCreateParams.builder()
//                    .setMode(SessionCreateParams.Mode.PAYMENT)
//                    .setSuccessUrl(returnUrl + "?session_id={CHECKOUT_SESSION_ID}")
//                    .setCancelUrl(returnUrl + "?canceled=true")
//                    .setClientReferenceId(referenceId)
//                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//                    .addLineItem(
//                            SessionCreateParams.LineItem.builder()
//                                    .setPriceData(
//                                            SessionCreateParams.LineItem.PriceData.builder()
//                                                    .setCurrency("lkr")
//                                                    .setUnitAmount(amountInCents)
//                                                    .setProductData(
//                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                                    .setName(description)
//                                                                    .build()
//                                                    )
//                                                    .build()
//                                    )
//                                    .setQuantity(1L)
//                                    .build()
//                    )
//                    .build();
//
//            // Create the Checkout Session
//            Session session = Session.create(params);
//
//            // Prepare response
//            Map<String, String> response = new HashMap<>();
//            response.put("transactionId", session.getId());
//            response.put("paymentLink", session.getUrl());
//            response.put("status", "pending");
//
//            log.info("Payment initiated with transaction ID: {}", session.getId());
//            return response;
//
//        } catch (StripeException e) {
//            log.error("Error initiating payment: {}", e.getMessage());
//            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public Map<String, String> processPayment(String referenceId, BigDecimal amount) {
//        log.info("Processing payment through Stripe: {}", referenceId);
//        try {
//            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
//
//            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                    .setCurrency("lkr")
//                    .setAmount(amountInCents)
//                    .setDescription("Payment for reference: " + referenceId)
//                    .setAutomaticPaymentMethods(
//                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                                    .setEnabled(true)
//                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
//                                    .build()
//                    )
//                    .addPaymentMethodType("card") // Restrict to card payments
//                    .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//            Map<String, String> response = new HashMap<>();
//            response.put("transactionId", paymentIntent.getId());
//            response.put("clientSecret", paymentIntent.getClientSecret()); // For client-side confirmation
//            response.put("status", paymentIntent.getStatus());
//
//            log.info("Payment processed with transaction ID: {}", paymentIntent.getId());
//            return response;
//
//        } catch (StripeException e) {
//            log.error("Error processing payment: {}", e.getMessage());
//            throw new PaymentException("Failed to process payment: " + e.getMessage());
//        }
//    }
//
////    @Override
////    public Map<String, String> processPayment(String referenceId, BigDecimal amount) {
////        log.info("Processing payment through Stripe: {}", referenceId);
////        try {
////            // Convert amount to cents
////            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
////
////            // Create a PaymentIntent for direct charging without redirect
////            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
////                    .setCurrency("usd")
////                    .setAmount(amountInCents)
////                    .setDescription("Payment for reference: " + referenceId)
////                    .setAutomaticPaymentMethods(
////                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
////                                    .setEnabled(true)
////                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
////                                    .build()
////                    )
////                    // For testing in dev, use a test card token
//////                    .setPaymentMethod("card") // Test payment method
////                    .addPaymentMethodType("card")
////                    .setConfirm(true)
////                    .build();
////
////            PaymentIntent paymentIntent = PaymentIntent.create(params);
////
////            // Prepare response
////            Map<String, String> response = new HashMap<>();
////            response.put("transactionId", paymentIntent.getId());
////            response.put("receiptUrl", "https://dashboard.stripe.com/test/payments/" + paymentIntent.getId());
////            response.put("status", paymentIntent.getStatus());
////
////            log.info("Payment processed with transaction ID: {}", paymentIntent.getId());
////            return response;
////
////        } catch (StripeException e) {
////            log.error("Error processing payment: {}", e.getMessage());
////            throw new PaymentException("Failed to process payment: " + e.getMessage());
////        }
////    }
//
//    @Override
//    public Map<String, String> processRefund(String transactionId, BigDecimal amount, String reason) {
//        log.info("Processing refund for transaction: {}", transactionId);
//        try {
//            // Convert amount to cents
//            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
//
//            // Create refund
//            RefundCreateParams params = RefundCreateParams.builder()
//                    .setPaymentIntent(transactionId)
//                    .setAmount(amountInCents)
//                    .setReason(convertToStripeRefundReason(reason))
//                    .build();
//
//            Refund refund = Refund.create(params);
//
//            // Prepare response
//            Map<String, String> response = new HashMap<>();
//            response.put("refundTransactionId", refund.getId());
//            response.put("status", refund.getStatus());
//            response.put("originalTransactionId", transactionId);
//
//            log.info("Refund processed with transaction ID: {}", refund.getId());
//            return response;
//
//        } catch (StripeException e) {
//            log.error("Error processing refund: {}", e.getMessage());
//            throw new PaymentException("Failed to process refund: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void cancelPayment(String transactionId) {
//        log.info("Cancelling payment for transaction: {}", transactionId);
//        try {
//            // Retrieve and cancel the payment intent
//            PaymentIntent paymentIntent = PaymentIntent.retrieve(transactionId);
//            if (paymentIntent.getStatus().equals("requires_payment_method") ||
//                    paymentIntent.getStatus().equals("requires_confirmation") ||
//                    paymentIntent.getStatus().equals("requires_action")) {
//
//                PaymentIntent canceledIntent = paymentIntent.cancel();
//                log.info("Payment cancelled for transaction ID: {}", canceledIntent.getId());
//            } else {
//                log.warn("Payment cannot be cancelled, status: {}", paymentIntent.getStatus());
//                throw new PaymentException("Cannot cancel payment with status: " + paymentIntent.getStatus());
//            }
//        } catch (StripeException e) {
//            log.error("Error cancelling payment: {}", e.getMessage());
//            throw new PaymentException("Failed to cancel payment: " + e.getMessage());
//        }
//    }
//
//    // Helper method to convert local reason to Stripe refund reason
//    private RefundCreateParams.Reason convertToStripeRefundReason(String reason) {
//        if (reason == null) {
//            return RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
//        }
//
//        reason = reason.toLowerCase();
//        if (reason.contains("duplicate")) {
//            return RefundCreateParams.Reason.DUPLICATE;
//        } else if (reason.contains("fraud")) {
//            return RefundCreateParams.Reason.FRAUDULENT;
//        } else {
//            return RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
//        }
//    }

    //v1
//    @Value("${payment.gateway.stripe.api.key}")
//    private String stripeApiKey;
//
//    @Value("${payment.gateway.stripe.api.url}")
//    private String stripeApiUrl;
//
//    @Override
//    public String processPayment(PaymentRequest paymentRequest) {
//        log.info("Processing payment through Stripe gateway for order ID: {}", paymentRequest.getOrderId());
//
//        try {
//            // For demonstration, we'll simulate a successful payment
//            // In a real implementation, this would integrate with Stripe's SDK or API
//
//            // Validate card details (for demo purposes)
//            validateCardDetails(paymentRequest);
//
//            // Simulate API call to Stripe
//            // In reality, we would use Stripe's SDK to process the payment
//
//            // For demo, generate a random transaction ID
//            String transactionId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
//
//            log.info("Payment processed successfully. Transaction ID: {}", transactionId);
//            return transactionId;
//
//        } catch (Exception e) {
//            log.error("Error processing payment through Stripe: {}", e.getMessage(), e);
//            throw new PaymentException("Failed to process payment: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public boolean processRefund(String transactionId) {
//        log.info("Processing refund for transaction ID: {}", transactionId);
//
//        try {
//            // Simulate refund process via Stripe API
//            // In reality, we would use Stripe's SDK to process the refund
//
//            // For demo, always return true
//            log.info("Refund processed successfully for transaction ID: {}", transactionId);
//            return true;
//
//        } catch (Exception e) {
//            log.error("Failed to process refund: {}", e.getMessage(), e);
//            throw new PaymentException("Failed to process refund: " + e.getMessage());
//        }
//    }
//
//    private void validateCardDetails(PaymentRequest paymentRequest) {
//        // Basic validation for card details (for demo purposes)
//        if (paymentRequest.getCardNumber() == null || paymentRequest.getCardNumber().trim().isEmpty()) {
//            throw new PaymentException("Card number is required");
//        }
//
//        if (paymentRequest.getExpiryDate() == null || paymentRequest.getExpiryDate().trim().isEmpty()) {
//            throw new PaymentException("Expiry date is required");
//        }
//
//        if (paymentRequest.getCvv() == null || paymentRequest.getCvv().trim().isEmpty()) {
//            throw new PaymentException("CVV is required");
//        }
//
//
//    }
}
