package com.sdp.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

//    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerName;

    private Long waiterId;

    private Integer tableNumber;

    private String specialInstructions;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private boolean isOnline;

    private String returnUrl;

    @NotEmpty(message = "Order must contain at least one item")
    private List<@Valid OrderItemRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Menu item ID is required")
        private Long menuItemId;

        @NotNull(message = "Menu item variant ID is required")
        private Long menuItemVariantId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        private String specialInstructions;

    }

}
