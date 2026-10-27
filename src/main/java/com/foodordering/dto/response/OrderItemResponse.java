package com.foodordering.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long foodItemId,
        String itemName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal lineTotal
) {
}
