package com.foodordering.repository;

import com.foodordering.entity.CustomerOrder;
import com.foodordering.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByUserIdOrderByOrderDateDesc(Long userId);
    Optional<CustomerOrder> findByIdAndUserId(Long id, Long userId);
    Page<CustomerOrder> findAllByOrderByOrderDateDesc(Pageable pageable);
    Page<CustomerOrder> findByStatusOrderByOrderDateDesc(OrderStatus status, Pageable pageable);

    @Query("SELECT COUNT(o) FROM CustomerOrder o")
    Long countTotalOrders();

    @Query("SELECT COUNT(o) FROM CustomerOrder o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CustomerOrder o WHERE o.status = com.foodordering.entity.enums.OrderStatus.DELIVERED")
    BigDecimal sumTotalSalesDelivered();
}
