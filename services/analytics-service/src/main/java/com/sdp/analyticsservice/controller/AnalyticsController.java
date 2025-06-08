package com.sdp.analyticsservice.controller;

import com.sdp.analyticsservice.dto.CategorySalesDto;
import com.sdp.analyticsservice.dto.DashboardAnalytics;
import com.sdp.analyticsservice.dto.MonthlySalesDto;
import com.sdp.analyticsservice.dto.TopProductDto;
import com.sdp.analyticsservice.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public String hi(){
        return "hiii";
    }
    @GetMapping("/dashboard")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<DashboardAnalytics> getDashboardAnalytics() {
        try {
            DashboardAnalytics analytics = analyticsService.getDashboardAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales-by-category")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<CategorySalesDto>> getSalesByCategory() {
        try {
            List<CategorySalesDto> salesByCategory = analyticsService.getSalesByCategory();
            return ResponseEntity.ok(salesByCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/monthly-sales")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<MonthlySalesDto>> getMonthlySales(
            @RequestParam(defaultValue = "12") int months) {
        try {
            List<MonthlySalesDto> monthlySales = analyticsService.getMonthlySales(months);
            return ResponseEntity.ok(monthlySales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-products")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(defaultValue = "3") int limit) {
        try {
            List<TopProductDto> topProducts = analyticsService.getTopProducts(limit);
            return ResponseEntity.ok(topProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryData() {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCategories", analyticsService.getTotalCategories());
            summary.put("totalCustomers", analyticsService.getTotalCustomers());
            summary.put("totalProductsSold", analyticsService.getTotalProductsSold());
            summary.put("totalRevenue", analyticsService.getTotalRevenue());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total-categories")
    public ResponseEntity<Integer> getTotalCategories() {
        try {
            int totalCategories = analyticsService.getTotalCategories();
            return ResponseEntity.ok(totalCategories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total-customers")
    public ResponseEntity<Integer> getTotalCustomers() {
        try {
            int totalCustomers = analyticsService.getTotalCustomers();
            return ResponseEntity.ok(totalCustomers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total-products-sold")
    public ResponseEntity<Integer> getTotalProductsSold() {
        try {
            int totalProductsSold = analyticsService.getTotalProductsSold();
            return ResponseEntity.ok(totalProductsSold);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        try {
            double totalRevenue = analyticsService.getTotalRevenue();
            return ResponseEntity.ok(totalRevenue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
