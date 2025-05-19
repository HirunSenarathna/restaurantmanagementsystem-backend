package com.sdp.orderservice.repository;

import com.sdp.orderservice.entity.Order;
import com.sdp.orderservice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByWaiterId(Long waiterId);

    List<Order> findByOrderStatus(OrderStatus status);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderTime BETWEEN :startDate AND :endDate")
    List<Order> findOrdersInTimeRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.isPaid = false")
    List<Order> findUnpaidOrders();

    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.orderTime DESC")
    List<Order> findRecentOrdersByCustomerId(@Param("customerId") Long customerId, Pageable pageable);



///

    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.orderStatus = 'DELIVERED' ORDER BY o.orderTime DESC")
    List<Order> findDeliveredOrdersByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customerId = :customerId AND o.orderTime >= :startDate")
    Long countOrdersByCustomerSince(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus != 'CANCELLED' AND o.orderTime >= :startDate AND o.orderTime <= :endDate")
    BigDecimal calculateRevenueForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    List<Order> findByIsOnline(boolean isOnline);
    Page<Order> findByIsOnline(boolean isOnline, Pageable pageable);
//    List<Order> findUnpaidOrdersByIsOnline(boolean isOnline);

    @Query("SELECT o FROM Order o WHERE o.isOnline = :isOnline AND o.isPaid = false")
    List<Order> findUnpaidOrdersByIsOnline(@Param("isOnline") boolean isOnline);



}
