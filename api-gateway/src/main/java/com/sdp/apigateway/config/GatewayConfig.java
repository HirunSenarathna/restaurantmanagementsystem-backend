//package com.sdp.apigateway.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("user-service", r -> r
//                        .path("/api/users/**")
//                        .uri("lb://user-service"))
//                .route("menu-service", r -> r
//                        .path("/api/menu/**")
//                        .uri("lb://menu-service"))
//                .route("order-service", r -> r
//                        .path("/api/orders/**")
//                        .uri("lb://order-service"))
//                .route("payment-service", r -> r
//                        .path("/api/payments/**")
//                        .uri("lb://payment-service"))
//                .route("feedback-service", r -> r
//                        .path("/api/feedback/**")
//                        .uri("lb://feedback-service"))
//                .route("analytics-service", r -> r
//                        .path("/api/analytics/**")
//                        .uri("lb://analytics-service"))
//                .build();
//    }
//}
