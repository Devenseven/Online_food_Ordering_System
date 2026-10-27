package com.foodordering.dto.response;

import java.math.BigDecimal;

public record FoodItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Boolean available,
        Boolean active,
        Long categoryId,
        String categoryName
) {
}
