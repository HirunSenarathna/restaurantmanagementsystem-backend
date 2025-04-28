package com.sdp.orderservice.exception;

import lombok.Data;

@Data
public class InsufficientItemQuantityException extends RuntimeException {

    private final Long menuItemId;
    private final String menuItemName;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    public InsufficientItemQuantityException(Long menuItemId, String menuItemName, Integer requestedQuantity, Integer availableQuantity) {
        super("Insufficient quantity of item: " + menuItemName + " with ID: " + menuItemId + " available quantity: " + availableQuantity + " requested quantity: " + requestedQuantity);
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
}
