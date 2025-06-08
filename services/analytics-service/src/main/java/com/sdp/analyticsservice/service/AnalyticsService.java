package com.sdp.analyticsservice.service;

import com.sdp.analyticsservice.client.CustomerServiceClient;
import com.sdp.analyticsservice.client.MenuServiceClient;
import com.sdp.analyticsservice.client.OrderServiceClient;
import com.sdp.analyticsservice.dto.*;
import com.sdp.analyticsservice.kafka.OrderKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyticsService {
    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private MenuServiceClient menuServiceClient;

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private OrderKafkaListener orderKafkaListener;

    public DashboardAnalytics getDashboardAnalytics() {
        DashboardAnalytics analytics = new DashboardAnalytics();
        analytics.setCategoryData(getSalesByCategory());
        analytics.setMonthlyData(getMonthlySales(12));
        analytics.setTopProductsData(getTopProducts(3));

        analytics.setTotalCategories(getTotalCategories());
        analytics.setTotalCustomers(getTotalCustomers());
        analytics.setTotalProductsSold(getTotalProductsSold());
        analytics.setTotalRevenue(getTotalRevenue());
        return analytics;
    }

    public List<CategorySalesDto> getSalesByCategory() {
        // Use Feign client to fetch completed orders
        List<OrderDTO> orders = orderServiceClient.getOrdersByStatus("CONFIRMED");
        Map<String, Double> categorySales = new HashMap<>();
        double totalSales = 0.0;

        for (OrderDTO order : orders) {
            for (OrderItemDTO item : order.getItems()) {
                MenuItemDTO menuItem = menuServiceClient.getMenuItemById(item.getMenuItemId());
                String category = menuItem.getCategoryName();
                double itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();
                categorySales.merge(category, itemTotal, Double::sum);
                totalSales += itemTotal;
            }
        }

        final double finalTotalSales = totalSales;
        return categorySales.entrySet().stream()
                .map(entry -> {
                    CategorySalesDto dto = new CategorySalesDto();
                    dto.setCategoryName(entry.getKey());
                    dto.setTotalSales(entry.getValue());
                    dto.setPercentage(finalTotalSales > 0 ? (entry.getValue() / finalTotalSales) * 100 : 0.0);
                    return dto;
                })
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales()))
                .collect(Collectors.toList());
    }

    public List<MonthlySalesDto> getMonthlySales(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);
        // Use Feign client to fetch orders within the time period
        List<OrderDTO> orders = orderServiceClient.getOrdersByStatusAndPeriod(
                "CONFIRMED",
                startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, Map<String, Double>> monthlyCategorySales = new TreeMap<>();
        for (OrderDTO order : orders) {
            String monthKey = order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            for (OrderItemDTO item : order.getItems()) {
                MenuItemDTO menuItem = menuServiceClient.getMenuItemById(item.getMenuItemId());
                String category = menuItem.getCategoryName();
                double itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();


                monthlyCategorySales.computeIfAbsent(monthKey, k -> new HashMap<>())
                        .merge(category, itemTotal, Double::sum);
            }
        }

        return monthlyCategorySales.entrySet().stream()
                .map(entry -> {
                    MonthlySalesDto dto = new MonthlySalesDto();
                    dto.setMonth(entry.getKey());
                    dto.setCategories(entry.getValue());
                    return dto;
                })
                .sorted(Comparator.comparing(MonthlySalesDto::getMonth))
                .collect(Collectors.toList());
    }

    public List<TopProductDto> getTopProducts(int limit) {
        // Use Feign client to fetch completed orders
        List<OrderDTO> orders = orderServiceClient.getOrdersByStatus("CONFIRMED");
        log.info(orders.toString());
        Map<Long, TopProductDto> productSales = new HashMap<>();

        for (OrderDTO order : orders) {
            for (OrderItemDTO item : order.getItems()) {
                MenuItemDTO menuItem = menuServiceClient.getMenuItemById(item.getMenuItemId());
                TopProductDto dto = productSales.computeIfAbsent(item.getMenuItemId(), k -> {
                    TopProductDto newDto = new TopProductDto();
                    newDto.setProductName(menuItem.getName());
                    newDto.setTotalSales(0L);
                    newDto.setOrderCount(0);
                    return newDto;
                });
                long itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).longValue();
                dto.setTotalSales(dto.getTotalSales() + itemTotal);
                dto.setOrderCount(dto.getOrderCount() + item.getQuantity());
            }
        }

        return productSales.values().stream()
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<CategorySalesDto> getRealTimeSalesByCategory() {
        // Use Kafka listener cache for real-time data
        List<OrderDTO> orders = orderKafkaListener.getOrderCache();
        Map<String, Double> categorySales = new HashMap<>();
        double totalSales = 0.0;

        for (OrderDTO order : orders) {
            if (!"CONFIRMED".equals(order.getOrderStatus())) continue;
            for (OrderItemDTO item : order.getItems()) {
                MenuItemDTO menuItem = menuServiceClient.getMenuItemById(item.getMenuItemId());
                String category = menuItem.getCategoryName();
                double itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();

                categorySales.merge(category, itemTotal, Double::sum);
                totalSales += itemTotal;
            }
        }

        final double finalTotalSales = totalSales;
        return categorySales.entrySet().stream()
                .map(entry -> {
                    CategorySalesDto dto = new CategorySalesDto();
                    dto.setCategoryName(entry.getKey());
                    dto.setTotalSales(entry.getValue());
                    dto.setPercentage(finalTotalSales > 0 ? (entry.getValue() / finalTotalSales) * 100 : 0.0);
                    return dto;
                })
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales()))
                .collect(Collectors.toList());
    }

    public int getTotalCategories() {
        List<MenuCategoryDTO> categories = menuServiceClient.getAllCategories();
        return categories.size();
    }

    public int getTotalCustomers() {
        try {
            List<CustomerResponse> customers = customerServiceClient.getAllCustomers();
            return customers.size();
        } catch (Exception e) {
            log.error("Error fetching total customers: ", e);
            return 0;
        }
    }

    public int getTotalProductsSold() {
        try {
            List<OrderDTO> orders = orderServiceClient.getOrdersByStatus("CONFIRMED");
            return orders.stream()
                    .flatMap(order -> order.getItems().stream())
                    .mapToInt(OrderItemDTO::getQuantity)
                    .sum();
        } catch (Exception e) {
            log.error("Error calculating total products sold: ", e);
            return 0;
        }
    }

    public double getTotalRevenue() {
        try {
            List<OrderDTO> orders = orderServiceClient.getOrdersByStatus("CONFIRMED");
            return orders.stream()
                    .flatMap(order -> order.getItems().stream())
                    .mapToDouble(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue())
                    .sum();
        } catch (Exception e) {
            log.error("Error calculating total revenue: ", e);
            return 0.0;
        }
    }
}
