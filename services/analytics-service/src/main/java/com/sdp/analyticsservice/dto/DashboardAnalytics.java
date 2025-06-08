package com.sdp.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalytics {

    private List<CategorySalesDto> categoryData;
    private List<MonthlySalesDto> monthlyData;
    private List<TopProductDto> topProductsData;

    private int totalCategories;
    private int totalCustomers;
    private int totalProductsSold;
    private double totalRevenue;
}
