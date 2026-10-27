package com.foodordering.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardResponse(
        Long totalOrders,
        Map<String, Long> ordersPerStatus,
        Long activeFoodItems,
        BigDecimal totalSales
) {
}
