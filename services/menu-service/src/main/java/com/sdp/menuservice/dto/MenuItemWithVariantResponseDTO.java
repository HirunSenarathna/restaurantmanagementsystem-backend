package com.sdp.menuservice.dto;

import lombok.Data;

@Data
public class MenuItemWithVariantResponseDTO {
    private Long menuItemId;
    private String menuItemName;
    private String description;
    private Long categoryId;
    private String categoryName;
    private boolean menuItemAvailable;

    private Long variantId;
    private String size;
    private String variant;
    private Double price;
    private Integer stockQuantity;
    private boolean variantAvailable;
}
