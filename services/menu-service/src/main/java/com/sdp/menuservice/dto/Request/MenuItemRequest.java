package com.sdp.menuservice.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {

    private String name;
    private String description;
    private Long categoryId;
    private boolean available;
}
