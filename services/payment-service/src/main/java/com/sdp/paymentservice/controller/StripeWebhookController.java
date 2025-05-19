package com.sdp.paymentservice.controller;

import com.sdp.paymentservice.exception.PaymentException;
import com.sdp.paymentservice.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.stripe.Stripe.apiKey;

@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${payment.gateway.stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        log.info("Received Stripe webhook");
        System.out.println( "Received Stripe webhook" );

        try {
            // Verify webhook signature and extract the event
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Get the object from the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                log.warn("Failed to deserialize Stripe event object");
                return ResponseEntity.badRequest().body("Failed to deserialize event object");
            }

            // Handle the event based on its type
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) stripeObject;
                    handleCheckoutSessionCompleted(session);
                    break;

                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    handlePaymentIntentSucceeded(paymentIntent);
                    break;

                case "payment_intent.payment_failed":
                    PaymentIntent failedPaymentIntent = (PaymentIntent) stripeObject;
                    handlePaymentIntentFailed(failedPaymentIntent);
                    break;

                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok().body("Webhook processed successfully");

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }

    private void handleCheckoutSessionCompleted(Session session) {
        String transactionId = session.getId();
        log.info("Checkout session completed: {}", transactionId);

        // Prepare callback parameters
        Map<String, String> gatewayParams = new HashMap<>();
        gatewayParams.put("receiptUrl", "https://dashboard.stripe.com/test/payments/" + transactionId);

        // Update payment status in our system
        paymentService.handlePaymentCallback(transactionId, "success", gatewayParams);
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        String transactionId = paymentIntent.getId();
        log.info("Payment intent succeeded: {}", transactionId);

        // Prepare callback parameters
        Map<String, String> gatewayParams = new HashMap<>();
        gatewayParams.put("receiptUrl", "https://dashboard.stripe.com/test/payments/" + transactionId);

        // Update payment status in our system
        paymentService.handlePaymentCallback(transactionId, "success", gatewayParams);
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        String transactionId = paymentIntent.getId();
        log.info("Payment intent failed: {}", transactionId);

        // Get error message if available
        String errorMessage = "Payment failed";
        if (paymentIntent.getLastPaymentError() != null) {
            errorMessage = paymentIntent.getLastPaymentError().getMessage();
        }

        // Prepare callback parameters
        Map<String, String> gatewayParams = new HashMap<>();
        gatewayParams.put("errorMessage", errorMessage);

        // Update payment status in our system
        paymentService.handlePaymentCallback(transactionId, "failed", gatewayParams);
    }

//    private final PaymentService paymentService;
//
//    @Value("${payment.gateway.stripe.webhook-secret}")
//    private String webhookSecret;
//
//    @PostConstruct
//    public void init() {
//        apiKey = apiKey;
//    }
//
//    public void handleWebhookEvent(String payload, String sigHeader) {
//        Event event = null;
//
//        try {
//            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
//        } catch (Exception e) {
//            log.error("Failed to verify webhook event", e);
//            throw new PaymentException("Webhook verification failed");
//        }
//
//        switch (event.getType()) {
//            case "checkout.session.completed":
//                handleCheckoutSessionCompleted((Session) event.getDataObjectDeserializer().getObject().get());
//                break;
//            case "payment_intent.succeeded":
//                handlePaymentIntentSucceeded((PaymentIntent) event.getDataObjectDeserializer().getObject().get());
//                break;
//            case "payment_intent.payment_failed":
//                handlePaymentIntentFailed((PaymentIntent) event.getDataObjectDeserializer().getObject().get());
//                break;
//            default:
//                log.debug("Unhandled event type: {}", event.getType());
//        }
//    }
//
//    private void handleCheckoutSessionCompleted(Session session) {
//        String transactionId = session.getId();
//        String orderId = session.getClientReferenceId(); // Get orderId from client_reference_id
//
//        if (orderId == null) {
//            log.error("No orderId found in checkout session");
//            return;
//        }
//
//        log.info("Checkout session completed: {}", transactionId);
//
//        Map<String, String> gatewayParams = new HashMap<>();
//        gatewayParams.put("receiptUrl", "https://dashboard.stripe.com/test/payments/" + transactionId);
//        gatewayParams.put("transactionId", transactionId);
//        gatewayParams.put("orderId", orderId); // Pass orderId to payment service
//
//        paymentService.handlePaymentCallback(transactionId, "success", gatewayParams);
//    }
//
//    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
//        String transactionId = paymentIntent.getId();
//        String orderId = paymentIntent.getMetadata().get("orderId"); // Get orderId from metadata
//
//        if (orderId == null) {
//            log.error("No orderId found in payment intent metadata");
//            return;
//        }
//
//        log.info("Payment intent succeeded: {}", transactionId);
//
//        Map<String, String> gatewayParams = new HashMap<>();
//        gatewayParams.put("receiptUrl", "https://dashboard.stripe.com/test/payments/" + transactionId);
//        gatewayParams.put("transactionId", transactionId);
//        gatewayParams.put("orderId", orderId); // Pass orderId to payment service
//
//        paymentService.handlePaymentCallback(transactionId, "success", gatewayParams);
//    }
//
//    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
//        String transactionId = paymentIntent.getId();
//        String orderId = paymentIntent.getMetadata().get("orderId"); // Get orderId from metadata
//
//        if (orderId == null) {
//            log.error("No orderId found in payment intent metadata");
//            return;
//        }
//
//        log.info("Payment intent failed: {}", transactionId);
//
//        Map<String, String> gatewayParams = new HashMap<>();
//        gatewayParams.put("transactionId", transactionId);
//        gatewayParams.put("orderId", orderId); // Pass orderId to payment service
//        gatewayParams.put("errorMessage", paymentIntent.getLastPaymentError() != null ?
//                paymentIntent.getLastPaymentError().getMessage() : "Unknown error");
//
//        paymentService.handlePaymentCallback(transactionId, "failed", gatewayParams);
//    }
}
