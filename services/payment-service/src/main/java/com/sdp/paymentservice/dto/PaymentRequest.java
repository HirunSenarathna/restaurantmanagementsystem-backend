package com.sdp.paymentservice.dto;

import com.sdp.paymentservice.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private Long orderId;
//    private Long customerId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private String returnUrl;
    private boolean isOnline;
    private Long processedBy;

//    private Long orderId;
//    private String customerId;
//    private double amount;
//    private PaymentMethod method;
//    private boolean online;
//    private String returnUrl;

//    @NotNull
//    private Long orderId;
//
//    @NotNull
//    private Long customerId;
//
//    @NotNull
//    @Positive
//    private BigDecimal amount;
//
//    @NotNull
//    private PaymentMethod method;
//
//    // Payment gateway specific fields
//    private String cardNumber;
//    private String cardHolderName;
//    private String expiryDate;
//    private String cvv;
//    private String billingAddress;
}
