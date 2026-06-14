package com.springstore.cart;

import com.springstore.cart.dto.AddCartItemRequest;
import com.springstore.cart.dto.CartResponse;
import com.springstore.cart.dto.UpdateCartItemRequest;
import com.springstore.product.ProductRepository;
import com.springstore.user.User;
import com.springstore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(Long userId) {
        var cart = findOrCreateCart(userId);
        var items = cartItemRepository.findByCartId(cart.getId());
        return toResponse(cart, items);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest req) {
        var cart = findOrCreateCart(userId);
        var product = productRepository.findById(req.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < req.quantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        var existingItems = cartItemRepository.findByCartId(cart.getId());
        var existing = existingItems.stream()
                .filter(item -> item.getProduct().getId().equals(req.productId()))
                .findFirst();

        if (existing.isPresent()) {
            var item = existing.get();
            var newQty = item.getQuantity() + req.quantity();
            if (product.getStock() < newQty) {
                throw new RuntimeException("Insufficient stock");
            }
            cartItemRepository.save(CartItem.builder()
                    .id(item.getId())
                    .cart(cart)
                    .product(product)
                    .quantity(newQty)
                    .build());
        } else {
            cartItemRepository.save(CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(req.quantity())
                    .build());
        }

        var items = cartItemRepository.findByCartId(cart.getId());
        return toResponse(cart, items);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest req) {
        var cart = findOrCreateCart(userId);
        var item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this user");
        }

        if (item.getProduct().getStock() < req.quantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        cartItemRepository.save(CartItem.builder()
                .id(item.getId())
                .cart(cart)
                .product(item.getProduct())
                .quantity(req.quantity())
                .build());

        var items = cartItemRepository.findByCartId(cart.getId());
        return toResponse(cart, items);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        var cart = findOrCreateCart(userId);
        var item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this user");
        }

        cartItemRepository.delete(item);
        var items = cartItemRepository.findByCartId(cart.getId());
        return toResponse(cart, items);
    }

    Cart findOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    var user = userRepository.getReferenceById(userId);
                    return cartRepository.save(Cart.builder().user(user).build());
                });
    }

    private CartResponse toResponse(Cart cart, List<CartItem> items) {
        var itemResponses = items.stream()
                .map(item -> new CartResponse.CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()))
                .toList();
        return new CartResponse(cart.getId(), itemResponses);
    }
}
