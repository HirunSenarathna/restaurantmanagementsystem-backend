package com.sdp.paymentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String customerId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

//    private Long id;
//    private Long customerId;
//    private String orderStatus;
//    private BigDecimal totalAmount;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private List<OrderItemDTO> items;
}
