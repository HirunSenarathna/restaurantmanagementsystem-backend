package com.sdp.paymentservice.repository;

import com.sdp.paymentservice.model.Payment;
import com.sdp.paymentservice.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByCustomerId(Long customerId);

    Page<Payment> findByCustomerId(String customerId, Pageable pageable);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByProcessedBy(Long employeeId);

    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> findByCustomerIdAndCreatedAtBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    //

    boolean existsByOrderId(Long orderId);

    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStatusAndCreatedAtBetween(PaymentStatus status, LocalDateTime start, LocalDateTime end);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    Optional<Payment> findByOrderIdAndTransactionId(Long orderId, String transactionId);
}
