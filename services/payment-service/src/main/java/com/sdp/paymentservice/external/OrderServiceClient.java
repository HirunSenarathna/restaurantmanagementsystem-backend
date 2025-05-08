package com.sdp.paymentservice.external;

import com.sdp.paymentservice.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "${application.config.order-url}")
public interface OrderServiceClient {

    @GetMapping("/{orderId}")
    OrderDTO getOrderById(@PathVariable("orderId") Long orderId);

    @PutMapping("/{orderId}/status")
    OrderDTO updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestParam("status") String status);
}
