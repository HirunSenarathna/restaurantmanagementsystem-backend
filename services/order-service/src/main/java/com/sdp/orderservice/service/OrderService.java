package com.sdp.orderservice.service;

import com.sdp.orderservice.dto.*;
import com.sdp.orderservice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);

    OrderDTO getOrderById(Long orderId);

    List<OrderDTO> getOrdersByCustomerId(Long customerId);

    Page<OrderDTO> getOrdersByCustomerId(Long customerId, Pageable pageable);

    List<OrderDTO> getOrdersByWaiterId(Long waiterId);

    List<OrderDTO> getOrdersByStatus(OrderStatus status);

    Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    OrderDTO updateOrderStatus(OrderStatusUpdateDTO orderStatusUpdateDTO);

    void cancelOrder(Long orderId, Long userId);

    List<OrderDTO> getOrdersForDate(LocalDate date);

    List<OrderDTO> getUnpaidOrders();

    OrderResponse reorderPreviousOrder(Long previousOrderId, Long customerId);

    //



    OrderDTO markOrderAsPaid(Long orderId, PaymentMethod paymentMethod, String transactionId);

    OrderDTO processInPersonPayment(Long orderId, PaymentRequest paymentRequest);


}
