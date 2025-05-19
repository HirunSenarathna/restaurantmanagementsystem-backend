package com.sdp.orderservice.controller;

import com.sdp.orderservice.dto.*;
import com.sdp.orderservice.entity.OrderStatus;
import com.sdp.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}/paged")
    public ResponseEntity<Page<OrderDTO>> getOrdersByCustomerPaged(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageRequest = PageRequest.of(page, size, sortDirection, sortBy);

        Page<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId, pageRequest);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/waiter/{waiterId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByWaiter(@PathVariable Long waiterId) {
        List<OrderDTO> orders = orderService.getOrdersByWaiterId(waiterId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatusPaged(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersByStatus(status, pageRequest);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Valid @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderStatusUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderDTO>> getOrdersForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<OrderDTO> orders = orderService.getOrdersForDate(date);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<OrderDTO>> getUnpaidOrders() {
        List<OrderDTO> unpaidOrders = orderService.getUnpaidOrders();
        return ResponseEntity.ok(unpaidOrders);
    }

    @PostMapping("/{previousOrderId}/reorder")
    public ResponseEntity<OrderResponse> reorderPreviousOrder(
            @PathVariable Long previousOrderId,
            @RequestParam Long customerId) {
        OrderResponse response = orderService.reorderPreviousOrder(previousOrderId, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //



    @PutMapping("/{orderId}/pay")
    public ResponseEntity<OrderDTO> markOrderAsPaid(
            @PathVariable Long orderId,
            @RequestParam PaymentMethod method,
            @RequestParam String transactionId
    ) {
        return ResponseEntity.ok(orderService.markOrderAsPaid(orderId, method, transactionId));
    }

    @PostMapping("/{orderId}/pay/in-person")
    public ResponseEntity<OrderDTO> processInPersonPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest paymentRequest
    ) {
        return ResponseEntity.ok(orderService.processInPersonPayment(orderId, paymentRequest));
    }

    @PutMapping("/{orderId}/payment-link")
    public ResponseEntity<Void> updateOrderWithPaymentLink(
            @PathVariable Long orderId,
            @RequestParam Long paymentId,
            @RequestParam String paymentLink
    ) {
        orderService.updateOrderWithPaymentLink(orderId, paymentId, paymentLink);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/online")
    public ResponseEntity<List<OrderDTO>> getOnlineOrders() {
        List<OrderDTO> orders = orderService.getOrdersByIsOnline(true);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("online/unpaid")
    public ResponseEntity<List<OrderDTO>> getOnlineUnpaidOrders() {
        List<OrderDTO> orders = orderService.getUnpaidOrdersByIsOnline(true);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/in-person")
    public ResponseEntity<List<OrderDTO>> getInPersonOrders() {
        List<OrderDTO> orders = orderService.getOrdersByIsOnline(false);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/online/paged")
    public ResponseEntity<Page<OrderDTO>> getOnlineOrdersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersByIsOnline(true, pageRequest);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/in-person/paged")
    public ResponseEntity<Page<OrderDTO>> getInPersonOrdersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersByIsOnline(false, pageRequest);
        return ResponseEntity.ok(orders);
    }





}
