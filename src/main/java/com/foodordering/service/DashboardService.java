package com.foodordering.service;

import com.foodordering.dto.response.DashboardResponse;
import com.foodordering.entity.enums.OrderStatus;
import com.foodordering.repository.CustomerOrderRepository;
import com.foodordering.repository.FoodItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final CustomerOrderRepository orderRepository;
    private final FoodItemRepository foodItemRepository;

    public DashboardService(CustomerOrderRepository orderRepository, FoodItemRepository foodItemRepository) {
        this.orderRepository = orderRepository;
        this.foodItemRepository = foodItemRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        Long totalOrders = orderRepository.countTotalOrders();
        Map<String, Long> ordersPerStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersPerStatus.put(status.name(), orderRepository.countByStatus(status));
        }
        Long activeFoodItems = foodItemRepository.findAll().stream()
                .filter(f -> Boolean.TRUE.equals(f.getActive()))
                .count();
        // Total sales as sum of total_amount for orders with status DELIVERED only
        BigDecimal totalSales = orderRepository.sumTotalSalesDelivered();
        if (totalSales == null) totalSales = BigDecimal.ZERO;
        return new DashboardResponse(totalOrders, ordersPerStatus, activeFoodItems, totalSales);
    }
}
