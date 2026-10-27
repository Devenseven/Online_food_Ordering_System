package com.foodordering.service;

import com.foodordering.dto.request.AddCartItemRequest;
import com.foodordering.dto.request.UpdateCartItemRequest;
import com.foodordering.entity.*;
import com.foodordering.entity.enums.Role;
import com.foodordering.exception.BadRequestException;
import com.foodordering.repository.CartItemRepository;
import com.foodordering.repository.CartRepository;
import com.foodordering.repository.FoodItemRepository;
import com.foodordering.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CartService cartService;

    @Test
    void addSameItemMergesQuantity() {
        User user = User.builder().id(1L).email("test@test.com").role(Role.CUSTOMER).build();
        Category category = Category.builder().id(1L).active(true).build();
        FoodItem foodItem = FoodItem.builder().id(1L).name("Pizza").price(BigDecimal.valueOf(10)).active(true).available(true).category(category).build();
        Cart cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
        CartItem existing = CartItem.builder().id(1L).cart(cart).foodItem(foodItem).quantity(2).build();
        cart.getItems().add(existing);

        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addItem("test@test.com", new AddCartItemRequest(1L, 3));

        assertEquals(5, existing.getQuantity());
    }

    @Test
    void quantityLimits() {
        User user = User.builder().id(1L).email("test@test.com").role(Role.CUSTOMER).build();
        Cart cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();

        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class, () -> cartService.addItem("test@test.com", new AddCartItemRequest(1L, 0)));
        assertThrows(BadRequestException.class, () -> cartService.addItem("test@test.com", new AddCartItemRequest(1L, 21)));
    }
}
