package com.foodordering.mapper;

import com.foodordering.dto.response.*;
import com.foodordering.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    UserResponse toUserResponse(User user);

    CategoryResponse toCategoryResponse(Category category);
    List<CategoryResponse> toCategoryResponseList(List<Category> categories);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    FoodItemResponse toFoodItemResponse(FoodItem foodItem);
    List<FoodItemResponse> toFoodItemResponseList(List<FoodItem> foodItems);

    @Mapping(target = "foodItemId", source = "foodItem.id")
    @Mapping(target = "foodItemName", source = "foodItem.name")
    @Mapping(target = "imageUrl", source = "foodItem.imageUrl")
    @Mapping(target = "unitPrice", source = "foodItem.price")
    @Mapping(target = "available", source = "foodItem.available")
    @Mapping(target = "active", source = "foodItem.active")
    @Mapping(target = "subtotal", source = "quantity", qualifiedByName = "calculateSubtotal")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Named("calculateSubtotal")
    default BigDecimal calculateSubtotal(Integer quantity) {
        return BigDecimal.ZERO;
    }

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "userEmail", source = "user.email")
    OrderResponse toOrderResponse(CustomerOrder order);

    @Mapping(target = "foodItemId", source = "foodItem.id")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);
}
