package com.sdp.paymentservice.dto;

import com.sdp.paymentservice.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSummaryResponse {
    private LocalDate date;
    private BigDecimal totalAmount;
    private int totalTransactions;
    private Map<PaymentMethod, Long> paymentsByMethod;
}
