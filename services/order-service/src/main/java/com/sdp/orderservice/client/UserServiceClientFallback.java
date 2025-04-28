package com.sdp.orderservice.client;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public ResponseEntity<Map<String, Object>> getCustomerById(Long id) {
        // Return a minimal fallback response
        Map<String, Object> fallbackUser = new HashMap<>();
        fallbackUser.put("id", id);
        fallbackUser.put("name", "Unknown User");
        fallbackUser.put("email", "unknown@example.com");
        return ResponseEntity.ok(fallbackUser);
    }

    @Override
    public ResponseEntity<String> getUserRole(Long id) {
        // Default role when user service is unavailable
        return ResponseEntity.ok("CUSTOMER");
    }
}
