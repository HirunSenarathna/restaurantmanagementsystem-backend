package com.sdp.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private boolean available;
    private String imageUrl;
    private List<MenuItemVariantDTO> variants;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemVariantDTO {
        private Long id;
        private String size;
        private String variant;
        private Double price;
        private Integer stockQuantity;
        private boolean available;
    }
}
