package com.springstore.order;

import com.springstore.cart.CartItemRepository;
import com.springstore.cart.CartService;
import com.springstore.order.dto.OrderResponse;
import com.springstore.product.Product;
import com.springstore.product.ProductRepository;
import com.springstore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse checkout(Long userId) {
        var user = userRepository.getReferenceById(userId);
        var cart = cartService.findOrCreateCart(userId);
        var cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (var ci : cartItems) {
            var product = ci.getProduct();
            if (product.getStock() < ci.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        var order = orderRepository.save(Order.builder()
                .user(user)
                .total(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .build());

        var total = BigDecimal.ZERO;
        for (var ci : cartItems) {
            var product = ci.getProduct();
            var unitPrice = product.getPrice();
            var subtotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(subtotal);

            orderItemRepository.save(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(ci.getQuantity())
                    .unitPrice(unitPrice)
                    .build());

            productRepository.save(Product.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stock(product.getStock() - ci.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .build());
        }

        order = Order.builder()
                .id(order.getId())
                .user(user)
                .total(total)
                .status(OrderStatus.PENDING)
                .createdAt(order.getCreatedAt())
                .build();
        orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());

        var items = orderItemRepository.findByOrderId(order.getId());
        return toResponse(order, items);
    }

    public Page<OrderResponse> findAllByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(order -> {
                    var items = orderItemRepository.findByOrderId(order.getId());
                    return toResponse(order, items);
                });
    }

    public OrderResponse findById(Long orderId, Long userId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Order does not belong to this user");
        }

        var items = orderItemRepository.findByOrderId(order.getId());
        return toResponse(order, items);
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        var itemResponses = items.stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotal(),
                order.getStatus().name(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
