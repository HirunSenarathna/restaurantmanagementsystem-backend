//package com.sdp.orderservice.client;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class MenuServiceClientFallback implements MenuServiceClient {
//
//    @Override
//    public ResponseEntity<Map<String, Object>> getMenuItemById(Long id) {
//        System.out.println("MenuServiceClientFallback: getMenuItemById");
//        // Return a minimal fallback response
//        Map<String, Object> fallbackItem = new HashMap<>();
//        fallbackItem.put("id", id);
//        fallbackItem.put("name", "Unknown Item");
//        fallbackItem.put("price", 0);
//        fallbackItem.put("description", "Temporary unavailable");
//        return ResponseEntity.ok(fallbackItem);
//    }
//
//    @Override
//    public ResponseEntity<Map<String, Object>> getMenuItemVariantById(Long id) {
//        // Return a minimal fallback response for variant
//        Map<String, Object> fallbackVariant = new HashMap<>();
//        fallbackVariant.put("id", id);
//        fallbackVariant.put("name", "Unknown Variant");
//        fallbackVariant.put("price", 0);
//        fallbackVariant.put("availableQuantity", 0);
//        return ResponseEntity.ok(fallbackVariant);
//    }
//
//    @Override
//    public ResponseEntity<Integer> getAvailableQuantity(Long id) {
//        // Fallback: assume no items available
//        return ResponseEntity.ok(0);
//    }
//
//    @Override
//    public ResponseEntity<Void> reduceMenuItemVariantQuantity(Long id, Integer amount) {
//        // Cannot reduce quantity in fallback mode
//        return ResponseEntity.ok().build();
//    }
//}
