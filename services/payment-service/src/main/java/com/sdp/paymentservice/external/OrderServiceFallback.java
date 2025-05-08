//package com.sdp.paymentservice.external;
//
//import com.sdp.paymentservice.exception.ServiceUnavailableException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class OrderServiceFallback implements OrderServiceClient {
//
//
//    @Override
//    public OrderDTO getOrderById(Long orderId) {
//        log.error("Order service is down. Unable to fetch order details for order ID: {}", orderId);
//        throw new ServiceUnavailableException("Order service is currently unavailable");
//    }
//
//    @Override
//    public OrderDTO updateOrderStatus(Long orderId, String status) {
//        log.error("Order service is down. Unable to update order status for order ID: {}", orderId);
//        throw new ServiceUnavailableException("Order service is currently unavailable");
//    }
//}
