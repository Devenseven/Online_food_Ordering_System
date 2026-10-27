package com.foodordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FoodItemRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must be at most 150 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        BigDecimal price,

        @Size(max = 500, message = "Image URL must be at most 500 characters")
        String imageUrl,

        Boolean available,

        Boolean active,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
