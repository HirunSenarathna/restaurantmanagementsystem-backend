package com.sdp.menuservice.service.kafka;

import com.sdp.menuservice.event.MenuItemEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuEventProducer {

    private final KafkaTemplate<String, MenuItemEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.menu-events}")
    private String menuEventsTopic;

    public void publishMenuItemCreatedEvent(Long menuItemId) {
        MenuItemEvent event = MenuItemEvent.menuItemCreated(menuItemId);
        kafkaTemplate.send(menuEventsTopic, String.valueOf(menuItemId), event);
    }

    public void publishMenuItemUpdatedEvent(Long menuItemId) {
        MenuItemEvent event = MenuItemEvent.menuItemUpdated(menuItemId);
        kafkaTemplate.send(menuEventsTopic, String.valueOf(menuItemId), event);
    }

    public void publishMenuItemDeletedEvent(Long menuItemId) {
        MenuItemEvent event = MenuItemEvent.menuItemDeleted(menuItemId);
        kafkaTemplate.send(menuEventsTopic, String.valueOf(menuItemId), event);
    }

    public void publishMenuItemStockUpdatedEvent(Long menuItemId, Long variantId) {
        MenuItemEvent event = MenuItemEvent.menuItemStockUpdated(menuItemId, variantId);
        kafkaTemplate.send(menuEventsTopic, String.valueOf(menuItemId), event);
    }

    public void publishMenuItemAvailabilityUpdatedEvent(Long menuItemId) {
        MenuItemEvent event = MenuItemEvent.menuItemAvailabilityUpdated(menuItemId);
        kafkaTemplate.send(menuEventsTopic, String.valueOf(menuItemId), event);
    }
}
