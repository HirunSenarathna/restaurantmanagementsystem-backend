package com.sdp.menuservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryDTO {

    private Long id;
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
