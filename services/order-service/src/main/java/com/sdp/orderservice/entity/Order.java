package com.sdp.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String customerName;

    private Long waiterId;

    private String waiterName;

    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime orderTime;

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime completionTime;

    private BigDecimal totalAmount;

    private String specialInstructions;

    private Boolean isPaid;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();


    // Helper method to add an item to the order
    public void addItem(OrderItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        items.add(item);
        item.setOrder(this);
    }


    // Helper method to remove an item from the order
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}
