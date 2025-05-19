package com.sdp.orderservice.service.impl;

import com.sdp.orderservice.client.MenuServiceClient;
import com.sdp.orderservice.client.PaymentServiceClient;
import com.sdp.orderservice.client.UserServiceClient;
import com.sdp.orderservice.dto.*;
import com.sdp.orderservice.entity.ItemSize;
import com.sdp.orderservice.entity.Order;
import com.sdp.orderservice.entity.OrderItem;
import com.sdp.orderservice.entity.OrderStatus;
import com.sdp.orderservice.exception.InsufficientItemQuantityException;
import com.sdp.orderservice.exception.MenuItemNotFoundException;
import com.sdp.orderservice.exception.OrderNotFoundException;
import com.sdp.orderservice.exception.PaymentException;
import com.sdp.orderservice.kafka.OrderCreatedEvent;
import com.sdp.orderservice.kafka.OrderEventProducer;
import com.sdp.orderservice.repository.OrderItemRepository;
import com.sdp.orderservice.repository.OrderRepository;
import com.sdp.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final MenuServiceClient menuServiceClient;
    private final UserServiceClient userServiceClient;
    private final OrderEventProducer orderEventProducer;
    private final PaymentServiceClient paymentServiceClient;


    private final OrderRepository orderRepository;


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating new order for customer: {}", orderRequest.getCustomerId());
        log.info("Order details: {}", orderRequest);

//        // 1. Verify customer exists
//        ResponseEntity<Map<String, Object>> customerResponse =
//                userServiceClient.getCustomerById(orderRequest.getCustomerId());
//        String customerName = customerResponse.getBody() != null ?
//                (String) customerResponse.getBody().get("firstname") : "Unknown Customer";
//
//        log.info("Customer name: {}", customerName);

        // 1. Handle customer (registered or walk-in)
        String customerName;
        if (orderRequest.getCustomerId() != null) {
            // Registered customer: Verify customer exists
            ResponseEntity<Map<String, Object>> customerResponse =
                    userServiceClient.getCustomerById(orderRequest.getCustomerId());
            if (customerResponse.getBody() == null) {
                throw new RuntimeException("Customer not found: " + orderRequest.getCustomerId());
            }
            customerName = (String) customerResponse.getBody().get("firstname");
        } else {
            // Walk-in customer: Use provided name or default
            customerName = orderRequest.getCustomerName() != null
                    ? orderRequest.getCustomerName()
                    : "Walk-in Customer";
        }

        // 2. Verify waiter exists if provided
        String waiterName = "Self Service";
        if (orderRequest.getWaiterId() != null) {
            ResponseEntity<Map<String, Object>> waiterResponse =
                    userServiceClient.getCustomerById(orderRequest.getWaiterId());
            waiterName = waiterResponse.getBody() != null ?
                    (String) waiterResponse.getBody().get("name") : "Unknown Waiter";
        }

        // 3. Create order entity
        Order order = Order.builder()
                .customerId(orderRequest.getCustomerId())
                .customerName(customerName)
                .waiterId(orderRequest.getWaiterId())
                .waiterName(waiterName)
                .tableNumber(orderRequest.getTableNumber())
                .orderStatus(OrderStatus.PLACED)
                .orderTime(LocalDateTime.now())
                .specialInstructions(orderRequest.getSpecialInstructions())
                .isPaid(false)
                .paymentMethod(orderRequest.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .isOnline(orderRequest.isOnline())
                .totalAmount(BigDecimal.ZERO) // Will calculate after adding items
                .build();

        log.info("Order entity created: {}", order);

        // 4. Process order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>(); // Create a separate list to collect items

        for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
            try {
                log.info("Processing order item: {}", itemRequest);

                // Get menu item details
                ResponseEntity<Map<String, Object>> menuItemResponse =
                        menuServiceClient.getMenuItemById(itemRequest.getMenuItemId());

                if (menuItemResponse.getBody() == null) {
                    throw new MenuItemNotFoundException("Menu item not found: " + itemRequest.getMenuItemId());
                }

                Map<String, Object> menuItem = menuItemResponse.getBody();
                String menuItemName = (String) menuItem.get("name");

                // Find the specific variant
                List<Map<String, Object>> variants = (List<Map<String, Object>>) menuItem.get("variants");
                Map<String, Object> variant = variants.stream()
                        .filter(v -> v.get("id").toString().equals(itemRequest.getMenuItemVariantId().toString()))
                        .findFirst()
                        .orElseThrow(() -> new MenuItemNotFoundException(
                                "Variant " + itemRequest.getMenuItemVariantId() + " not found"));

                String variantName = variant.get("variant") != null ?
                        (String) variant.get("variant") : "Standard";
                String sizeStr = (String) variant.get("size");
                ItemSize size = ItemSize.valueOf(sizeStr);
                BigDecimal price = new BigDecimal(variant.get("price").toString());

                // Check quantity
                Integer availableQuantity = (Integer) variant.get("stockQuantity");
                if (availableQuantity < itemRequest.getQuantity()) {
                    throw new InsufficientItemQuantityException(
                            itemRequest.getMenuItemVariantId(),
                            menuItemName + " (" + variantName + ", " + size + ")",
                            itemRequest.getQuantity(),
                            availableQuantity
                    );
                }

                // Create OrderItem
                BigDecimal subTotal = price.multiply(new BigDecimal(itemRequest.getQuantity()));
                totalAmount = totalAmount.add(subTotal);

                OrderItem orderItem = OrderItem.builder()
                        .menuItemId(itemRequest.getMenuItemId())
                        .menuItemName(menuItemName)
                        .menuItemVariantId(itemRequest.getMenuItemVariantId())
                        .variant(variantName)
                        .size(size)
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(price)
                        .subTotal(subTotal)
                        .specialInstructions(itemRequest.getSpecialInstructions())
                        .build();

                log.info("Order item created: {}", orderItem);
                order.addItem(orderItem);

                // Reduce available quantity in menu service
                menuServiceClient.reduceMenuItemVariantQuantity(
                        itemRequest.getMenuItemVariantId(),
                        itemRequest.getQuantity()
                );

            } catch (Exception e) {
                log.error("Error processing order item: {}", itemRequest, e);
                throw e; // Re-throw to fail the transaction
            }
        }

        log.info("order items: {}", order.getItems());
        log.info("total amount after processing order items: {}", totalAmount);

        // 5. Set total amount and save order
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);


        // 6. Publish order created event with payment info if needed
        boolean requiresPayment = orderRequest.getPaymentMethod() != PaymentMethod.CASH;

        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .customerId(savedOrder.getCustomerId())
                .totalAmount(savedOrder.getTotalAmount())
                .paymentMethod(orderRequest.getPaymentMethod())
                .requiresPayment(requiresPayment)
                .returnUrl(orderRequest.getReturnUrl())
                .orderTime(savedOrder.getOrderTime())
                .build();

        orderEventProducer.publishOrderCreatedEvent(orderCreatedEvent);

        // 7. Return response (without payment details initially)
        OrderResponse response = OrderResponse.builder()
                .orderId(savedOrder.getId())
                .customerId(savedOrder.getCustomerId())
                .customerName(savedOrder.getCustomerName())
                .tableNumber(savedOrder.getTableNumber())
                .orderStatus(savedOrder.getOrderStatus())
                .orderTime(savedOrder.getOrderTime())
                .totalAmount(savedOrder.getTotalAmount())
                .paymentId(savedOrder.getPaymentId())
                .paymentLink(savedOrder.getPaymentLink())
                .isOnline(savedOrder.isOnline())
//                .items(mapToOrderItemDTOs(savedOrder.getItems()))
                .message(requiresPayment ?
                        "Order placed successfully. Payment processing initiated." :
                        "Order placed successfully.")
                .build();

        return response;

    }



    @Override
    @Transactional
    public void updateOrderPaymentStatus(Long orderId, boolean isPaid, PaymentMethod method, String transactionId, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setIsPaid(isPaid);
        order.setPaymentMethod(method);
        order.setTransactionId(transactionId);
        order.setPaymentStatus(paymentStatus);

        // Update order status based on payment status
        if (isPaid && paymentStatus == PaymentStatus.COMPLETED) {
            if (order.getOrderStatus() == OrderStatus.PLACED) {
                order.setOrderStatus(OrderStatus.CONFIRMED); // Transition to CONFIRMED after payment
            }
        }

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        log.info("Order {} payment status updated to paid with order status {}", orderId, order.getOrderStatus());
    }

    @Override
    @Transactional
    public void recordPaymentFailure(Long orderId, String errorMessage) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setErrorMessage(errorMessage);
        orderRepository.save(order);
        log.info("Payment failure recorded for order {}: {}", orderId, errorMessage);
    }

    @Override
    @Transactional
    public void updateOrderWithPaymentLink(Long orderId, Long paymentId, String paymentLink) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setPaymentId(paymentId);
        order.setPaymentLink(paymentLink);
        orderRepository.save(order);
        log.info("Order {} updated with payment link", orderId);
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapToOrderDTO(order);
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public Page<OrderDTO> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByCustomerId(customerId, pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    public List<OrderDTO> getOrdersByWaiterId(Long waiterId) {
        List<Order> orders = orderRepository.findByWaiterId(waiterId);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByOrderStatus(status);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByOrderStatus(status, pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    public List<OrderDTO> getOrdersByIsOnline(boolean isOnline) {
        List<Order> orders = orderRepository.findByIsOnline(isOnline);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public Page<OrderDTO> getOrdersByIsOnline(boolean isOnline, Pageable pageable) {
        Page<Order> orders = orderRepository.findByIsOnline(isOnline, pageable);
        List<OrderDTO> orderDTOs = orders.getContent().stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(orderDTOs, pageable, orders.getTotalElements());
    }

    @Override
    public List<OrderDTO> getUnpaidOrdersByIsOnline(boolean isOnline) {
        List<Order> orders = orderRepository.findUnpaidOrdersByIsOnline(isOnline);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(OrderStatusUpdateDTO updateDTO) {
        Order order = orderRepository.findById(updateDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(updateDTO.getOrderId()));

        // Update status
        order.setOrderStatus(updateDTO.getNewStatus());
        order.setUpdatedAt(LocalDateTime.now());

        // If status is DELIVERED or CANCELLED, set completion time
        if (updateDTO.getNewStatus() == OrderStatus.DELIVERED ||
                updateDTO.getNewStatus() == OrderStatus.CANCELLED) {
            order.setUpdatedAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);

        // Publish order status updated event
//        orderEventProducer.publishOrderStatusUpdatedEvent(updatedOrder);

        return mapToOrderDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO markOrderAsPaid(Long orderId, PaymentMethod paymentMethod, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Update payment status via updateOrderPaymentStatus
        updateOrderPaymentStatus(orderId, true, paymentMethod, transactionId, PaymentStatus.COMPLETED);

        Order updatedOrder = orderRepository.findById(orderId).get();

        // Publish order updated event
//        orderEventProducer.publishOrderStatusUpdatedEvent(updatedOrder);

        return mapToOrderDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO processInPersonPayment(Long orderId, PaymentRequest paymentRequest) {

        log.info("Processing in-person payment for order: {}", orderId);

        // Retrieve order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Prevent duplicate payment
        if (order.getIsPaid()) {
            log.warn("Order {} is already paid.", orderId);
            return mapToOrderDTO(order); // Return the current state of the order
        }

        // Verify payment amount matches order amount
        if (paymentRequest.getAmount().compareTo(order.getTotalAmount()) != 0) {
            log.error("Payment amount {} does not match order amount {}",
                    paymentRequest.getAmount(), order.getTotalAmount());
            throw new PaymentException("Payment amount does not match order amount");
        }

        // Set orderId and isOnline flag in payment request
        paymentRequest.setOrderId(orderId);
        paymentRequest.setIsOnline(false); // This is an in-person payment

        // Process payment through PaymentService
        PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest);
        log.info(paymentRequest.toString());

        // Verify payment status
        if (paymentResponse.getStatus() != PaymentStatus.COMPLETED) {
            log.error("Payment failed for order {}: {}", orderId, paymentResponse.getMessage());
            throw new PaymentException("Payment failed: " + paymentResponse.getMessage());
        }

        // Mark order as paid
        return markOrderAsPaid(orderId, paymentRequest.getMethod(), paymentResponse.getTransactionId());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Check if the user is the customer who placed the order or has appropriate role
        if (!order.getCustomerId().equals(userId)) {
            ResponseEntity<String> userRoleResponse = userServiceClient.getUserRole(userId);
            String role = userRoleResponse.getBody();

            if (role == null || (!role.equals("ADMIN") && !role.equals("OWNER") &&
                    !role.equals("WAITER") && !role.equals("CASHIER"))) {
                throw new RuntimeException("Unauthorized to cancel this order");
            }
        }

        // Only cancel if order is not already delivered or cancelled
        if (order.getOrderStatus() != OrderStatus.DELIVERED &&
                order.getOrderStatus() != OrderStatus.CANCELLED) {

            // Return items to inventory
            for (OrderItem item : order.getItems()) {
                menuServiceClient.reduceMenuItemVariantQuantity(
                        item.getMenuItemVariantId(),
                        -item.getQuantity()  // Negative value to increase quantity
                );
            }

            // If payment was made, initiate refund
            if (order.getIsPaid() && order.getPaymentMethod() != PaymentMethod.CASH) {
                try {
                    // Call payment service to refund
                    RefundRequest refundRequest = RefundRequest.builder()
                            .orderId(orderId)
                            .amount(order.getTotalAmount())
                            .reason("Order cancelled")
                            .build();

                    paymentServiceClient.refundPayment(refundRequest);
                } catch (Exception e) {
                    log.error("Error processing refund: {}", e.getMessage());
                    // Continue with cancellation even if refund fails
                    // Manual intervention may be needed
                }
            }

            // Update order status
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());

            Order cancelledOrder = orderRepository.save(order);

            // Publish order status updated event
//            orderEventProducer.publishOrderStatusUpdatedEvent(cancelledOrder);
        } else {
            throw new RuntimeException("Cannot cancel order that is already delivered or cancelled");
        }
    }

    @Override
    public List<OrderDTO> getOrdersForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findOrdersInTimeRange(startOfDay, endOfDay);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getUnpaidOrders() {
        List<Order> unpaidOrders = orderRepository.findUnpaidOrders();
        return unpaidOrders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse reorderPreviousOrder(Long previousOrderId, Long customerId) {
        // 1. Find previous order
        Order previousOrder = orderRepository.findById(previousOrderId)
                .orElseThrow(() -> new OrderNotFoundException(previousOrderId));

        // 2. Verify the customer is reordering their own order
        if (!previousOrder.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Cannot reorder someone else's order");
        }

        // 3. Create new order request from previous order
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setTableNumber(previousOrder.getTableNumber());
        orderRequest.setSpecialInstructions(previousOrder.getSpecialInstructions());
        orderRequest.setPaymentMethod(previousOrder.getPaymentMethod());

        List<OrderRequest.OrderItemRequest> itemRequests = new ArrayList<>();
        for (OrderItem previousItem : previousOrder.getItems()) {
            OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
            itemRequest.setMenuItemId(previousItem.getMenuItemId());
            itemRequest.setMenuItemVariantId(previousItem.getMenuItemVariantId());
            itemRequest.setQuantity(previousItem.getQuantity());
            itemRequest.setSpecialInstructions(previousItem.getSpecialInstructions());
            itemRequests.add(itemRequest);
        }
        orderRequest.setItems(itemRequests);

        // 4. Create new order
        return createOrder(orderRequest);
    }

    // Helper method to map Order entity to OrderDTO
    private OrderDTO mapToOrderDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItemId())
                        .menuItemName(item.getMenuItemName())
                        .variant(item.getVariant())
                        .size(item.getSize())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subTotal(item.getSubTotal())
                        .specialInstructions(item.getSpecialInstructions())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .waiterId(order.getWaiterId())
                .waiterName(order.getWaiterName())
                .tableNumber(order.getTableNumber())
                .orderStatus(order.getOrderStatus())
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .specialInstructions(order.getSpecialInstructions())
                .isPaid(order.getIsPaid())
                .paymentMethod(order.getPaymentMethod())
                .transactionId(order.getTransactionId())
                .paymentLink(order.getPaymentLink())
                .items(itemDTOs)
                .isOnline(order.isOnline())
                .build();
    }


}
