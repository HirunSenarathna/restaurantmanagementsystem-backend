package com.sdp.analyticsservice.client;

import com.sdp.analyticsservice.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service",  url = "${application.config.customer-url}")
public interface CustomerServiceClient {

    @GetMapping
    List<CustomerResponse> getAllCustomers();
}
