package com.sdp.notification_service.service;

import com.sdp.notification_service.client.UserServiceClient;
import com.sdp.notification_service.dto.UserResponse;
import com.sdp.notification_service.model.Notification;
import com.sdp.notification_service.model.NotificationType;
import com.sdp.notification_service.model.UserRole;
import com.sdp.orderservice.kafka.OrderCreatedEvent;
import com.sdp.paymentservice.kafka.PaymentCompletedEvent;
import com.sdp.paymentservice.kafka.PaymentFailedEvent;
import com.sdp.paymentservice.kafka.PaymentInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventListenerService {

    private final NotificationService notificationService;
    private final UserServiceClient userServiceClient;

    @KafkaListener(topics = "${kafka.topics.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received order created event for order: {}", event.getOrderId());
        processOrderCreated(event);
    }

    @KafkaListener(topics = "${kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Received payment completed event for payment: {}", event.getPaymentId());
        log.info(event.toString());
        processPaymentCompleted(event);
    }

    @KafkaListener(topics = "${kafka.topics.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        log.info("Received payment failed event for payment: {}", event.getPaymentId());
        processPaymentFailed(event);
    }

    @KafkaListener(topics = "${kafka.topics.payment-initiated}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentInitiatedEvent(PaymentInitiatedEvent event) {
        log.info("Received payment initiated event for payment: {}", event.getPaymentId());
        processPaymentInitiated(event);
    }

    @KafkaListener(topics = "menu-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handleMenuEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        log.info("Received menu event: {}", eventType);

        if ("MENU_UPDATED".equals(eventType)) {
            processMenuUpdated(event);
        } else if ("LOW_INVENTORY".equals(eventType)) {
            processLowInventory(event);
        } else {
            log.warn("Unhandled menu event type: {}", eventType);
        }
    }

    private void processOrderCreated(OrderCreatedEvent event) {
        String orderId = String.valueOf(event.getOrderId());
        Long customerId = event.getCustomerId();


        // Get customer info from user service
        UserResponse customerInfo = userServiceClient.getUserById(customerId);

        // Notify customer
        Notification customerNotification = new Notification(
                String.valueOf(customerInfo.getId()),
                customerInfo.getEmail(),
                customerInfo.getPhone(),
                NotificationType.ORDER_PLACED,
                "Order Placed Successfully",
                "Your order #" + orderId + " has been received and is being processed.",
                UserRole.CUSTOMER
        );
        notificationService.sendNotification(customerNotification);

        // Notify waiters
        Notification waiterNotification = new Notification(
                null, // broadcast to all waiters
                null,
                null,
                NotificationType.ORDER_PLACED,
                "New Order Received",
                "A new order #" + orderId + " has been placed and requires attention.",
                UserRole.WAITER
        );
        notificationService.broadcastToRole(waiterNotification, UserRole.WAITER);
    }

    private void processPaymentCompleted(PaymentCompletedEvent event) {
        String orderId = String.valueOf(event.getOrderId());
        Long customerId = event.getCustomerId();

        String paymentId = String.valueOf(event.getPaymentId());
        log.info("Processing PaymentCompletedEvent for paymentId: {}, orderId: {}, customerId: {}",
                paymentId, orderId, customerId);

        if (customerId == null) {
            log.warn("No customerId provided in PaymentCompletedEvent for payment: {}", paymentId);
            return;
        }

        // Get customer info
        UserResponse customerInfo = userServiceClient.getUserById(customerId);
        log.info(customerInfo.toString());

        // Notify customer
        Notification notification = new Notification(
                String.valueOf(customerInfo.getId()),
                customerInfo.getEmail(),
                customerInfo.getPhone(),
                NotificationType.PAYMENT_SUCCESSFUL,
                "Payment Successful",
                "Your payment of " + event.getAmount() + " for order #" + orderId + " was successful.",
                UserRole.CUSTOMER
        );
        notificationService.sendNotification(notification);
    }

    private void processPaymentFailed(PaymentFailedEvent event) {
        String orderId = String.valueOf(event.getOrderId());
        Long customerId = event.getCustomerId();

        // Get customer info
        UserResponse customerInfo = userServiceClient.getUserById(customerId);

        // Notify customer
        Notification notification = new Notification(
                String.valueOf(customerInfo.getId()),
                customerInfo.getEmail(),
                customerInfo.getPhone(),
                NotificationType.PAYMENT_FAILED,
                "Payment Failed",
                "Your payment for order #" + orderId + " failed: " + event.getErrorMessage(),
                UserRole.CUSTOMER
        );
        notificationService.sendNotification(notification);
    }

    private void processPaymentInitiated(PaymentInitiatedEvent event) {
        String orderId = String.valueOf(event.getOrderId());
        Long customerId = event.getCustomerId();

        if (customerId == null) {
            log.warn("No customerId provided in PaymentInitiatedEvent for order: {}", orderId);
            return; // Skip notification if customerId is missing
        }

        // Get customer info
        UserResponse customerInfo = userServiceClient.getUserById(customerId);

        // Notify customer
        Notification notification = new Notification(
                String.valueOf(customerInfo.getId()),
                customerInfo.getEmail(),
                customerInfo.getPhone(),
                NotificationType.PAYMENT_INITIATED,
                "Payment Initiated",
                "Your payment for order #" + orderId + " has been initiated. Payment link: " + event.getPaymentLink(),
                UserRole.CUSTOMER
        );
        notificationService.sendNotification(notification);
    }

    private void processMenuUpdated(Map<String, Object> event) {
        Notification notification = new Notification(
                null,
                null,
                null,
                NotificationType.MENU_UPDATED,
                "Menu Updated",
                "The menu has been updated with new items.",
                UserRole.OWNER
        );
        notificationService.broadcastToRole(notification, UserRole.OWNER);
    }

    private void processLowInventory(Map<String, Object> event) {
        String itemName = (String) event.get("itemName");
        Integer currentQuantity = (Integer) event.get("currentQuantity");

        Notification notification = new Notification(
                null,
                null,
                null,
                NotificationType.LOW_INVENTORY,
                "Low Inventory Alert",
                "Item \"" + itemName + "\" is running low. Current quantity: " + currentQuantity,
                UserRole.OWNER
        );
        notificationService.broadcastToRole(notification, UserRole.OWNER);
    }
}
