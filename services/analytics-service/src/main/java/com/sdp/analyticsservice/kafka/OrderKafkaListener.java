package com.sdp.analyticsservice.kafka;

import com.sdp.analyticsservice.dto.OrderDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderKafkaListener {
    // In-memory cache for real-time analytics (simplified for example)
    private static final List<OrderDTO> orderCache = new ArrayList<>();

    @KafkaListener(topics = "order-updates", groupId = "analytics-group")
    public void listenOrderUpdates(OrderDTO order) {
        // Add to cache or process for real-time analytics
        orderCache.add(order);
        // Optional: Trigger analytics recalculation or updates
    }

    public List<OrderDTO> getOrderCache() {
        return orderCache;
    }
}
