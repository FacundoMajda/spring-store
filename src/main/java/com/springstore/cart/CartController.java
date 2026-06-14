package com.springstore.cart;

import com.springstore.cart.dto.AddCartItemRequest;
import com.springstore.cart.dto.CartResponse;
import com.springstore.cart.dto.UpdateCartItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication auth) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication auth, @Valid @RequestBody AddCartItemRequest req) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.addItem(userId, req));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication auth,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest req) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, req));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(Authentication auth, @PathVariable Long itemId) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }
}
