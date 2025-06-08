package com.sdp.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDto {

    private String productName;
    private Long totalSales;
    private Integer orderCount;
}
