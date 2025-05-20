package com.sdp.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long customerId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private boolean isOnline;


    @Column
    private Long processedBy;

    private String transactionId;
    private String paymentLink;
    private String receiptUrl;
    private String paymentGatewayResponse;

    private BigDecimal refundAmount;
    private String refundReason;
    private String refundTransactionId;
    private String refundResponse;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private Long orderId;
//
//    @Column(nullable = false)
//    private Long customerId;
//
//    @Column(nullable = false)
//    private BigDecimal amount;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private PaymentStatus status;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private PaymentMethod method;
//
//    @Column
//    private String transactionId;
//
//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column
//    private LocalDateTime updatedAt;
//
//    @Column
//    private Long processedBy; // Employee ID (cashier) if processed in-person
//
//    // Pre-persist hook
//    @PrePersist
//    public void prePersist() {
//        this.createdAt = LocalDateTime.now();
//        if (this.status == null) {
//            this.status = PaymentStatus.PENDING;
//        }
//
//    }
//
//    // Pre-update hook
//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
}
