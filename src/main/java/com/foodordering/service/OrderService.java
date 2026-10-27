package com.foodordering.service;

import com.foodordering.dto.request.PlaceOrderRequest;
import com.foodordering.dto.response.OrderResponse;
import com.foodordering.dto.response.OrderStatusResponse;
import com.foodordering.entity.*;
import com.foodordering.entity.enums.OrderStatus;
import com.foodordering.entity.enums.PaymentMethod;
import com.foodordering.entity.enums.PaymentStatus;
import com.foodordering.exception.BadRequestException;
import com.foodordering.exception.InvalidStatusTransitionException;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.mapper.EntityMapper;
import com.foodordering.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final CustomerOrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    public OrderService(CustomerOrderRepository orderRepository, CartRepository cartRepository,
                        FoodItemRepository foodItemRepository, UserRepository userRepository, EntityMapper mapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public OrderResponse placeOrder(String email, PlaceOrderRequest request) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Cart is empty"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Reload and validate each item
        List<CartItem> validatedItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(cartItem.getFoodItem().getId())
                    .orElseThrow(() -> new BadRequestException("Item no longer available: " + cartItem.getFoodItem().getName()));
            if (!Boolean.TRUE.equals(foodItem.getActive())) {
                throw new BadRequestException("Item is not active: " + foodItem.getName());
            }
            if (foodItem.getCategory() == null || !Boolean.TRUE.equals(foodItem.getCategory().getActive())) {
                throw new BadRequestException("Item's category is not active: " + foodItem.getName());
            }
            if (!Boolean.TRUE.equals(foodItem.getAvailable())) {
                throw new BadRequestException("Item is currently unavailable: " + foodItem.getName());
            }
            cartItem.setFoodItem(foodItem); // use latest
            validatedItems.add(cartItem);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : validatedItems) {
            BigDecimal lineTotal = ci.getFoodItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(deliveryFee).setScale(2, RoundingMode.HALF_UP);
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        CustomerOrder order = CustomerOrder.builder()
                .user(user)
                .status(OrderStatus.PLACED)
                .deliveryAddress(request.deliveryAddress())
                .contactNumber(request.contactNumber())
                .deliveryInstructions(request.deliveryInstructions())
                .subtotal(subtotal)
                .deliveryFee(deliveryFee.setScale(2, RoundingMode.HALF_UP))
                .totalAmount(total)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        for (CartItem ci : validatedItems) {
            FoodItem fi = ci.getFoodItem();
            BigDecimal unitPrice = fi.getPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity())).setScale(2, RoundingMode.HALF_UP);
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .foodItem(fi)
                    .itemName(fi.getName())
                    .unitPrice(unitPrice)
                    .quantity(ci.getQuantity())
                    .lineTotal(lineTotal)
                    .build();
            order.getOrderItems().add(orderItem);
        }

        CustomerOrder savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return mapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = getUser(email);
        List<CustomerOrder> orders = orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
        return orders.stream().map(mapper::toOrderResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String email, Long orderId) {
        User user = getUser(email);
        CustomerOrder order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderStatusResponse getOrderStatus(String email, Long orderId) {
        User user = getUser(email);
        CustomerOrder order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return new OrderStatusResponse(order.getId(), order.getStatus().name(), order.getOrderDate(), order.getTotalAmount());
    }

    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        User user = getUser(email);
        CustomerOrder order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Order can only be cancelled while in PLACED status");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return mapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(OrderStatus status) {
        List<CustomerOrder> orders;
        if (status == null) {
            orders = orderRepository.findAllByOrderByOrderDateDesc(org.springframework.data.domain.PageRequest.of(0, 1000)).getContent();
        } else {
            orders = orderRepository.findByStatusOrderByOrderDateDesc(status, org.springframework.data.domain.PageRequest.of(0, 1000)).getContent();
        }
        return orders.stream().map(mapper::toOrderResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String statusStr) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr);
        } catch (Exception e) {
            throw new BadRequestException("Invalid status");
        }
        validateTransition(order.getStatus(), newStatus, true);
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        return mapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrderByAdmin(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        validateTransition(order.getStatus(), OrderStatus.CANCELLED, true);
        order.setStatus(OrderStatus.CANCELLED);
        return mapper.toOrderResponse(orderRepository.save(order));
    }

    private void validateTransition(OrderStatus current, OrderStatus next, boolean isAdmin) {
        if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot change status from " + current);
        }
        if (next == OrderStatus.CANCELLED) {
            if (current == OrderStatus.PLACED) {
                return;
            }
            if (isAdmin && current == OrderStatus.CONFIRMED) {
                return;
            }
            throw new InvalidStatusTransitionException("Not allowed to cancel from " + current);
        }
        switch (current) {
            case PLACED -> {
                if (next == OrderStatus.CONFIRMED) return;
                throw new InvalidStatusTransitionException("Invalid transition");
            }
            case CONFIRMED -> {
                if (next == OrderStatus.PREPARING) return;
                throw new InvalidStatusTransitionException("Invalid transition");
            }
            case PREPARING -> {
                if (next == OrderStatus.OUT_FOR_DELIVERY) return;
                throw new InvalidStatusTransitionException("Invalid transition");
            }
            case OUT_FOR_DELIVERY -> {
                if (next == OrderStatus.DELIVERED) return;
                throw new InvalidStatusTransitionException("Invalid transition");
            }
            default -> throw new InvalidStatusTransitionException("Invalid transition");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
