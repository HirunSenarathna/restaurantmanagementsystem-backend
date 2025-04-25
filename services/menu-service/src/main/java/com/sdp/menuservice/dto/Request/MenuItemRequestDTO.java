package com.sdp.menuservice.dto.Request;

import com.sdp.menuservice.model.ItemSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequestDTO {
    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private List<VariantRequest> variants;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantRequest {
        @NotNull(message = "Size is required")
        private ItemSize size;

        private String variant;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private BigDecimal price;

        @NotNull(message = "Stock quantity is required")
        @Positive(message = "Stock quantity must be positive")
        private Integer stockQuantity;

        private boolean available = true;
    }
}
