package com.sdp.orderservice.repository;

import com.sdp.orderservice.entity.Order;
import com.sdp.orderservice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


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
}
