package com.foodordering.service;

import com.foodordering.dto.request.AddCartItemRequest;
import com.foodordering.dto.request.UpdateCartItemRequest;
import com.foodordering.dto.response.CartItemResponse;
import com.foodordering.dto.response.CartResponse;
import com.foodordering.entity.Cart;
import com.foodordering.entity.CartItem;
import com.foodordering.entity.FoodItem;
import com.foodordering.entity.User;
import com.foodordering.exception.BadRequestException;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.repository.CartItemRepository;
import com.foodordering.repository.CartRepository;
import com.foodordering.repository.FoodItemRepository;
import com.foodordering.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       FoodItemRepository foodItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getOrCreateCart(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).items(new ArrayList<>()).build();
            return cartRepository.save(newCart);
        });
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(Cart.builder().user(user).items(new ArrayList<>()).build()));
        if (request.quantity() < 1 || request.quantity() > 20) {
            throw new BadRequestException("Quantity must be between 1 and 20");
        }
        FoodItem foodItem = foodItemRepository.findById(request.foodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        if (!Boolean.TRUE.equals(foodItem.getActive()) || !Boolean.TRUE.equals(foodItem.getCategory().getActive())) {
            throw new BadRequestException("This item is not available");
        }
        if (!Boolean.TRUE.equals(foodItem.getAvailable())) {
            throw new BadRequestException("This item is currently unavailable");
        }

        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getFoodItem().getId().equals(foodItem.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + request.quantity();
            if (newQty > 20) throw new BadRequestException("Quantity must be at most 20 per item");
            existing.setQuantity(newQty);
        } else {
            CartItem cartItem = CartItem.builder().cart(cart).foodItem(foodItem).quantity(request.quantity()).build();
            cart.getItems().add(cartItem);
        }
        Cart saved = cartRepository.save(cart);
        return buildCartResponse(saved);
    }

    @Transactional
    public CartResponse updateItem(String email, Long cartItemId, UpdateCartItemRequest request) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (request.quantity() < 1 || request.quantity() > 20) {
            throw new BadRequestException("Quantity must be between 1 and 20");
        }
        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItem.setQuantity(request.quantity());
        Cart saved = cartRepository.save(cart);
        return buildCartResponse(saved);
    }

    @Transactional
    public void removeItem(String email, Long cartItemId) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().removeIf(ci -> ci.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private User getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            FoodItem fi = ci.getFoodItem();
            BigDecimal unitPrice = fi.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(subtotal);
            items.add(new CartItemResponse(
                    ci.getId(),
                    fi.getId(),
                    fi.getName(),
                    fi.getImageUrl(),
                    unitPrice,
                    ci.getQuantity(),
                    subtotal,
                    fi.getAvailable(),
                    fi.getActive()
            ));
        }
        return new CartResponse(cart.getId(), items, total, items.size());
    }
}
