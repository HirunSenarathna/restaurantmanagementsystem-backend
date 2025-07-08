package com.sdp.analyticsservice.controller;

import com.sdp.analyticsservice.dto.CategorySalesDto;
import com.sdp.analyticsservice.dto.DashboardAnalytics;
import com.sdp.analyticsservice.dto.MonthlySalesDto;
import com.sdp.analyticsservice.dto.TopProductDto;
import com.sdp.analyticsservice.service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
    public ResponseEntity<DashboardAnalytics> getDashboardAnalytics() {
        try {
            DashboardAnalytics analytics = analyticsService.getDashboardAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales-by-category")

    public ResponseEntity<List<CategorySalesDto>> getSalesByCategory() {
        try {
            List<CategorySalesDto> salesByCategory = analyticsService.getSalesByCategory();
            return ResponseEntity.ok(salesByCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/monthly-sales")

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

    @GetMapping("/sales-by-category/filtered")
    public ResponseEntity<List<CategorySalesDto>> getSalesByCategoryFiltered(
            @RequestParam String filterType,
            @RequestParam String filterValue) {
        try {
            List<CategorySalesDto> sales = analyticsService.getSalesByCategoryWithTimeFilter(filterType, filterValue);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            log.error("Error fetching filtered category sales: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available-years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        try {
            List<Integer> years = analyticsService.getAvailableYears();
            return ResponseEntity.ok(years);
        } catch (Exception e) {
            log.error("Error fetching available years: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales-by-category/year/{year}")
    public ResponseEntity<List<CategorySalesDto>> getSalesByCategoryByYear(@PathVariable String year) {
        try {
            List<CategorySalesDto> sales = analyticsService.getSalesByCategoryWithTimeFilter("year", year);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            log.error("Error fetching category sales by year: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales-by-category/month/{yearMonth}")
    public ResponseEntity<List<CategorySalesDto>> getSalesByCategoryByMonth(@PathVariable String yearMonth) {
        try {
            List<CategorySalesDto> sales = analyticsService.getSalesByCategoryWithTimeFilter("month", yearMonth);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            log.error("Error fetching category sales by month: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
