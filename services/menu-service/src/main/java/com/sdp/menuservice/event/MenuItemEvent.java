package com.sdp.menuservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemEvent {

    private String eventType;
    private Long menuItemId;
    private Long variantId;
    private LocalDateTime timestamp;

    public static MenuItemEvent menuItemCreated(Long menuItemId) {
        return new MenuItemEvent("MENU_ITEM_CREATED", menuItemId, null, LocalDateTime.now());
    }

    public static MenuItemEvent menuItemUpdated(Long menuItemId) {
        return new MenuItemEvent("MENU_ITEM_UPDATED", menuItemId, null, LocalDateTime.now());
    }

    public static MenuItemEvent menuItemDeleted(Long menuItemId) {
        return new MenuItemEvent("MENU_ITEM_DELETED", menuItemId, null, LocalDateTime.now());
    }

    public static MenuItemEvent menuItemStockUpdated(Long menuItemId, Long variantId) {
        return new MenuItemEvent("MENU_ITEM_STOCK_UPDATED", menuItemId, variantId, LocalDateTime.now());
    }

    public static MenuItemEvent menuItemAvailabilityUpdated(Long menuItemId) {
        return new MenuItemEvent("MENU_ITEM_AVAILABILITY_UPDATED", menuItemId, null, LocalDateTime.now());
    }
}
