package com.sdp.menuservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class MenuItemVariantResponseDTO {
    private Long menuItemId;
    private String size;
    private String variant;
    private BigDecimal price;
    private Integer stockQuantity;
    private boolean available;
}