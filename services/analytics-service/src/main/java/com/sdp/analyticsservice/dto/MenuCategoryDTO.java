package com.sdp.analyticsservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryDTO {

    private Long id;

    private String name;

    private String description;
}
