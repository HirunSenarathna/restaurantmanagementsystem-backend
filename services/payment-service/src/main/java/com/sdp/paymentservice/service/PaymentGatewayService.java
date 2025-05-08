package com.sdp.paymentservice.service;

import com.sdp.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayService {


//    String processPayment(PaymentRequest paymentRequest);
//
//    boolean processRefund(String transactionId);

    //
    Map<String, String> initiatePayment(String referenceId, BigDecimal amount, String description, String returnUrl);
    Map<String, String> processPayment(String referenceId, BigDecimal amount);
    Map<String, String> processRefund(String transactionId, BigDecimal amount, String reason);
    void cancelPayment(String transactionId);
}
