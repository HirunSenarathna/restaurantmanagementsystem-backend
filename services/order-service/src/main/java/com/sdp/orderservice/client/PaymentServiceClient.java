package com.sdp.orderservice.client;

import com.sdp.orderservice.dto.PaymentRequest;
import com.sdp.orderservice.dto.PaymentResponse;
import com.sdp.orderservice.dto.RefundRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",  url = "${application.config.payment-url}")
public interface PaymentServiceClient {

    @PostMapping("/create")
    PaymentResponse createPayment(@RequestBody PaymentRequest paymentRequest);

    @PostMapping("/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest);

    @PostMapping("/refund")
    void refundPayment(@RequestBody RefundRequest refundRequest);

    @GetMapping("/{paymentId}")
    PaymentResponse getPaymentStatus(@PathVariable Long paymentId);
}
