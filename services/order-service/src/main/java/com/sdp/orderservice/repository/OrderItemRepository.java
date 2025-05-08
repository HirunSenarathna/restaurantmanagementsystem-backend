package com.sdp.orderservice.repository;

import com.sdp.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    List<OrderItem> findByMenuItemId(@Param("menuItemId") Long menuItemId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    Integer getTotalOrderedQuantityForMenuItem(@Param("menuItemId") Long menuItemId);

    //


    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.orderTime >= :startDate AND o.orderTime <= :endDate AND o.orderStatus != 'CANCELLED'")
    List<OrderItem> findOrderItemsInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT oi.menuItemId, oi.menuItemName, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi JOIN oi.order o " +
            "WHERE o.orderTime >= :startDate AND o.orderTime <= :endDate AND o.orderStatus != 'CANCELLED' " +
            "GROUP BY oi.menuItemId, oi.menuItemName " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findPopularMenuItems(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
