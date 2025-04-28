package com.sdp.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "menu-service",  url = "${application.config.menu-url}")
public interface MenuServiceClient {


    @GetMapping("/items/{id}")
    ResponseEntity<Map<String, Object>> getMenuItemById(@PathVariable("id") Long id);

    @GetMapping("/menu-item-variants/{id}")
    ResponseEntity<Map<String, Object>> getMenuItemVariantById(@PathVariable("id") Long id);

    @GetMapping("/menu-item-variants/{id}/available-quantity")
    ResponseEntity<Integer> getAvailableQuantity(@PathVariable("id") Long id);

    @PutMapping("/menu-item-variants/{id}/quantity/reduce")
    ResponseEntity<Void> reduceMenuItemVariantQuantity(
            @PathVariable("id") Long id,
            @RequestParam("amount") Integer amount);
}
