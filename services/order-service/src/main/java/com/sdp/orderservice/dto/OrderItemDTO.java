package com.sdp.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private String variant;
//    private ItemSize size;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    private String specialInstructions;
}
