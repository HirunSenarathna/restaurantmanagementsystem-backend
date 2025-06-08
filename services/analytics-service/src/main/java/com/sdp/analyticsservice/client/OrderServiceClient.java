package com.sdp.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sdp.analyticsservice.dto.OrderDTO;
import java.util.List;

@FeignClient(name = "menu-service",  url = "${application.config.orders-url}")
public interface OrderServiceClient {
    @GetMapping("/status/{status}")
    List<OrderDTO> getOrdersByStatus(@RequestParam("status") String status);

    @GetMapping("/period")
    List<OrderDTO> getOrdersByStatusAndPeriod(
            @RequestParam("status") String status,
            @RequestParam("start") String start,
            @RequestParam("end") String end);
}
