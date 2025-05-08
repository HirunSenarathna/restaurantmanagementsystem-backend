package com.sdp.paymentservice.service;

import com.sdp.paymentservice.dto.PaymentRequest;
import com.sdp.paymentservice.dto.PaymentResponse;
import com.sdp.paymentservice.dto.PaymentSummaryResponse;
import com.sdp.paymentservice.dto.RefundRequest;
import com.sdp.paymentservice.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PaymentService {

//    PaymentResponse processPayment(PaymentRequest paymentRequest);
//
//    PaymentResponse getPaymentDetails(Long paymentId);
//
//    PaymentResponse getPaymentByOrderId(Long orderId);
//
//    List<PaymentResponse> getCustomerPayments(Long customerId);
//
//    Page<PaymentResponse> getCustomerPaymentsPaginated(Long customerId, Pageable pageable);
//
//    PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus status);
//
//    PaymentResponse refundPayment(Long paymentId);
//
//    List<PaymentResponse> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate);
//
//    List<PaymentResponse> getPaymentsByEmployee(Long employeeId);

    //

    PaymentResponse createPayment(PaymentRequest paymentRequest);
    PaymentResponse processPayment(PaymentRequest paymentRequest);
    PaymentResponse handlePaymentCallback(String transactionId, String status, Map<String, String> gatewayParams);
    PaymentResponse getPaymentById(Long paymentId);
    List<PaymentResponse> getPaymentsByOrderId(Long orderId);
    List<PaymentResponse> getPaymentsByCustomerId(Long customerId);
    PaymentResponse refundPayment(Long paymentId, RefundRequest refundRequest);
    Page<PaymentResponse> getAllPayments(Pageable pageable);
    PaymentSummaryResponse getPaymentSummary(LocalDate date);
    PaymentResponse cancelPayment(Long paymentId);
}
