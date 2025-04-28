package com.sdp.orderservice.service.impl;

import com.sdp.orderservice.client.MenuServiceClient;
import com.sdp.orderservice.client.UserServiceClient;
import com.sdp.orderservice.dto.*;
import com.sdp.orderservice.entity.ItemSize;
import com.sdp.orderservice.entity.Order;
import com.sdp.orderservice.entity.OrderItem;
import com.sdp.orderservice.entity.OrderStatus;
import com.sdp.orderservice.exception.InsufficientItemQuantityException;
import com.sdp.orderservice.exception.MenuItemNotFoundException;
import com.sdp.orderservice.exception.OrderNotFoundException;
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

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuServiceClient menuServiceClient;
    private final UserServiceClient userServiceClient;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating new order for customer: {}", orderRequest.getCustomerId());
        log.info("Order details: {}", orderRequest);

        // 1. Verify customer exists
        ResponseEntity<Map<String, Object>> customerResponse =
                userServiceClient.getCustomerById(orderRequest.getCustomerId());
        String customerName = customerResponse.getBody() != null ?
                (String) customerResponse.getBody().get("name") : "Unknown Customer";

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
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30)) // Default 30 min
                .specialInstructions(orderRequest.getSpecialInstructions())
                .isPaid(false)
                .totalAmount(BigDecimal.ZERO) // Will calculate after adding items
                .build();

        log.info("Order entity created: {}", order);




        // 4.Process order items
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

        // 6. Publish order created event
        OrderDTO orderDTO = mapToOrderDTO(savedOrder);
        orderEventProducer.publishOrderCreatedEvent(orderDTO);

        // 7. Return response

        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .customerId(savedOrder.getCustomerId())
                .customerName(savedOrder.getCustomerName())
                .tableNumber(savedOrder.getTableNumber())
                .orderStatus(savedOrder.getOrderStatus())
                .orderTime(savedOrder.getOrderTime())
                .estimatedDeliveryTime(savedOrder.getEstimatedDeliveryTime())
                .totalAmount(savedOrder.getTotalAmount())
                .items(orderDTO.getItems())
                .message("Order placed successfully")
                .build();
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
    @Transactional
    public OrderDTO updateOrderStatus(OrderStatusUpdateDTO updateDTO) {
        Order order = orderRepository.findById(updateDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(updateDTO.getOrderId()));

        // Update status
        order.setOrderStatus(updateDTO.getNewStatus());

        // If status is DELIVERED or CANCELLED, set completion time
        if (updateDTO.getNewStatus() == OrderStatus.DELIVERED ||
                updateDTO.getNewStatus() == OrderStatus.CANCELLED) {
            order.setCompletionTime(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);

        // Publish order status updated event
        orderEventProducer.publishOrderStatusUpdatedEvent(updatedOrder);

        return mapToOrderDTO(updatedOrder);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {

    }
//
//    @Override
//    @Transactional
//    public void cancelOrder(Long orderId, Long userId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(orderId));
//
//        // Check if the user is the customer who placed the order or has appropriate role
//        if (!order.getCustomerId().equals(userId)) {
//            ResponseEntity<String> userRoleResponse = userServiceClient.getUserRole(userId);
//            String role = userRoleResponse.getBody();
//
//            if (role == null || (!role.equals("ADMIN") && !role.equals("OWNER") &&
//                    !role.equals("WAITER") && !role.equals("CASHIER"))) {
//                throw new RuntimeException("Unauthorized to cancel this order");
//            }
//        }
//
//        // Only cancel if order is not already delivered or cancelled
//        if (order.getOrderStatus() != OrderStatus.DELIVERED &&
//                order.getOrderStatus() != OrderStatus.CANCELLED) {
//
//            // Return items to inventory
//            for (OrderItem item : order.getItems()) {
//                menuServiceClient.reduceMenuItemQuantity(
//                        item.getMenuItemId(),
//                        -item.getQuantity()  // Negative value to increase quantity
//                );
//            }
//
//            // Update order status
//            order.setOrderStatus(OrderStatus.CANCELLED);
//            order.setCompletionTime(LocalDateTime.now());
//
//            Order cancelledOrder = orderRepository.save(order);
//
//            // Publish order status updated event
//            orderEventProducer.publishOrderStatusUpdatedEvent(cancelledOrder);
//        } else {
//            throw new RuntimeException("Cannot cancel order that is already delivered or cancelled");
//        }
//    }

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

        List<OrderRequest.OrderItemRequest> itemRequests = new ArrayList<>();
        for (OrderItem previousItem : previousOrder.getItems()) {
            OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
            itemRequest.setMenuItemId(previousItem.getMenuItemId());
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
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .completionTime(order.getCompletionTime())
                .totalAmount(order.getTotalAmount())
                .specialInstructions(order.getSpecialInstructions())
                .isPaid(order.getIsPaid())
                .items(itemDTOs)
                .build();
    }

}
