package com.sdp.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class, url = "${application.config.customer-url}")
public interface UserServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable("id") Long id);

    @GetMapping("/api/users/{id}/role")
    ResponseEntity<String> getUserRole(@PathVariable("id") Long id);
}
