package com.sdp.orderservice.kafka;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuUpdateConsumer {
    @KafkaListener(topics = "${kafka.topics.menu-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMenuUpdateEvent(JsonNode menuUpdateEvent) {
        try {
            log.info("Received menu update event: {}", menuUpdateEvent);

            Long menuItemId = menuUpdateEvent.get("menuItemId").asLong();
            Integer newAvailableQuantity = menuUpdateEvent.get("availableQuantity").asInt();
            String menuItemName = menuUpdateEvent.get("name").asText();

            log.info("Menu item {} (ID: {}) updated. New available quantity: {}",
                    menuItemName, menuItemId, newAvailableQuantity);

            //  could implement logic to handle menu updates
            //  check if any pending orders need to be adjusted

        } catch (Exception e) {
            log.error("Error processing menu update event: {}", e.getMessage(), e);
        }
    }
}
