package com.sdp.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long menuItemId;

    private String menuItemName;

    private Long menuItemVariantId;

    private String variant;

    @Enumerated(EnumType.STRING)
    private ItemSize size;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;

    private String specialInstructions;
}
