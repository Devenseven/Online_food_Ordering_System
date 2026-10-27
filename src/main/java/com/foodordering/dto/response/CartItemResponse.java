package com.foodordering.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long foodItemId,
        String foodItemName,
        String imageUrl,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        Boolean available,
        Boolean active
) {
}
