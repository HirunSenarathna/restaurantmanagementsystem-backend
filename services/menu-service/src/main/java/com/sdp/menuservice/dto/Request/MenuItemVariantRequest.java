package com.sdp.menuservice.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemVariantRequest {

    private Long menuItemId;
    private String variant;
    private String size;
    private String price;
    private Integer stockQuantity;
    private boolean available;
}
