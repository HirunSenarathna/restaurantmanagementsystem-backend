package com.sdp.orderservice.entity;

import com.sdp.orderservice.dto.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = false)
    private Long customerId;

    private String customerName;

    private Long waiterId;

    private String waiterName;

    @Column(nullable = false)
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @CreationTimestamp
    private LocalDateTime orderTime;


    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(length = 500)
    private String specialInstructions;

    @Column(nullable = false)
    private Boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String transactionId;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
    }

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long customerId;
//    private String customerName;
//    private Long waiterId;
//    private String waiterName;
//    private Long cashierId;
//    private String cashierName;
//
//    private Integer tableNumber;
//
//    @Enumerated(EnumType.STRING)
//    private OrderStatus orderStatus;
//
//    private LocalDateTime orderTime;
//    private LocalDateTime estimatedDeliveryTime;
//    private LocalDateTime completionTime;
//
//    private String specialInstructions;
//
//    private Boolean isPaid;
//    private String paymentMethod;
//    private String paymentStatus;
//    private String paymentId;
//    private LocalDateTime paymentTime;
//
//    private BigDecimal totalAmount;
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<OrderItem> items;
//
//    public void addItem(OrderItem item) {
//        item.setOrder(this);
//        this.items.add(item);
//    }
}
