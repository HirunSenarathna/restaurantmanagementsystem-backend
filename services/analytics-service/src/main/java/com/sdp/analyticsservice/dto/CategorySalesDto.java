package com.sdp.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySalesDto {
    private String categoryName;
    private Double totalSales;
    private Double percentage;

}
