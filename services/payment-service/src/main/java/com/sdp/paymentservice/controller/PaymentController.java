package com.sdp.paymentservice.controller;

import com.sdp.paymentservice.dto.PaymentRequest;
import com.sdp.paymentservice.dto.PaymentResponse;
import com.sdp.paymentservice.dto.PaymentSummaryResponse;
import com.sdp.paymentservice.dto.RefundRequest;
import com.sdp.paymentservice.model.PaymentStatus;
import com.sdp.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        log.info("Creating payment order: {}", paymentRequest.getOrderId());
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        log.info("Processing in-person payment for order: {}", paymentRequest.getOrderId());
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        log.info("Fetching payment with id: {}", paymentId);
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payments for order: {}", orderId);
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/processedBy/{processedBy}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByProcessedBy(@PathVariable Long processedBy) {
        log.info("Fetching payments for customer: {}", processedBy);
        List<PaymentResponse> responses = paymentService.getPaymentsByProcessedBy(processedBy);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long paymentId,
            @RequestBody RefundRequest refundRequest) {
        log.info("Refunding payment: {}", paymentId);
        PaymentResponse response = paymentService.refundPayment(paymentId, refundRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);
        PaymentResponse response = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(Pageable pageable) {
        log.info("Fetching all payments with pagination");
        Page<PaymentResponse> responses = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/summary")
    public ResponseEntity<PaymentSummaryResponse> getPaymentSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("Fetching payment summary for date: {}", date);
        PaymentSummaryResponse response = paymentService.getPaymentSummary(date);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<PaymentResponse> handlePaymentCallback(
            @RequestParam("transactionId") String transactionId,
            @RequestParam("status") String status,
            @RequestBody Map<String, String> gatewayParams) {
        log.info("Received payment callback for transaction: {}", transactionId);
        PaymentResponse response = paymentService.handlePaymentCallback(transactionId, status, gatewayParams);
        return ResponseEntity.ok(response);
    }

//    private final PaymentService paymentService;
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
//        log.info("Received request to process payment for order ID: {}", paymentRequest.getOrderId());
//        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
//        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
//    }
//
//    @GetMapping("/{paymentId}")
//    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable Long paymentId) {
//        log.info("Fetching payment details for payment ID: {}", paymentId);
//        PaymentResponse paymentResponse = paymentService.getPaymentDetails(paymentId);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
//        log.info("Fetching payment details for order ID: {}", orderId);
//        PaymentResponse paymentResponse = paymentService.getPaymentByOrderId(orderId);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @GetMapping("/customer/{customerId}")
//    public ResponseEntity<List<PaymentResponse>> getCustomerPayments(@PathVariable Long customerId) {
//        log.info("Fetching payment history for customer ID: {}", customerId);
//        List<PaymentResponse> paymentResponses = paymentService.getCustomerPayments(customerId);
//        return ResponseEntity.ok(paymentResponses);
//    }
//
//    @GetMapping("/customer/{customerId}/paginated")
//    public ResponseEntity<Page<PaymentResponse>> getCustomerPaymentsPaginated(
//            @PathVariable Long customerId,
//            Pageable pageable) {
//        log.info("Fetching paginated payment history for customer ID: {}", customerId);
//        Page<PaymentResponse> paymentResponses = paymentService.getCustomerPaymentsPaginated(customerId, pageable);
//        return ResponseEntity.ok(paymentResponses);
//    }
//
//    @PatchMapping("/{paymentId}/status")
//    @PreAuthorize("hasRole('CASHIER') or hasRole('OWNER')")
//    public ResponseEntity<PaymentResponse> updatePaymentStatus(
//            @PathVariable Long paymentId,
//            @RequestParam PaymentStatus status) {
//        log.info("Updating payment status to {} for payment ID: {}", status, paymentId);
//        PaymentResponse paymentResponse = paymentService.updatePaymentStatus(paymentId, status);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @PostMapping("/{paymentId}/refund")
//    @PreAuthorize("hasRole('CASHIER') or hasRole('OWNER')")
//    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long paymentId) {
//        log.info("Processing refund for payment ID: {}", paymentId);
//        PaymentResponse paymentResponse = paymentService.refundPayment(paymentId);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @GetMapping("/date-range")
//    @PreAuthorize("hasRole('CASHIER') or hasRole('OWNER')")
//    public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        log.info("Fetching payments between {} and {}", startDate, endDate);
//        List<PaymentResponse> paymentResponses = paymentService.getPaymentsByDateRange(startDate, endDate);
//        return ResponseEntity.ok(paymentResponses);
//    }
//
//    @GetMapping("/employee/{employeeId}")
//    @PreAuthorize("hasRole('OWNER')")
//    public ResponseEntity<List<PaymentResponse>> getPaymentsByEmployee(@PathVariable Long employeeId) {
//        log.info("Fetching payments processed by employee ID: {}", employeeId);
//        List<PaymentResponse> paymentResponses = paymentService.getPaymentsByEmployee(employeeId);
//        return ResponseEntity.ok(paymentResponses);
//    }
}
