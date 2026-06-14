package com.springstore.cart;

import com.springstore.cart.dto.AddCartItemRequest;
import com.springstore.cart.dto.CartResponse;
import com.springstore.cart.dto.UpdateCartItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart", description = "Returns the cart with all items")
    public ResponseEntity<CartResponse> getCart(Authentication auth) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to the cart. Creates the cart if it doesn't exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added"),
            @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<CartResponse> addItem(Authentication auth, @Valid @RequestBody AddCartItemRequest req) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.addItem(userId, req));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Changes the quantity of an existing cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    public ResponseEntity<CartResponse> updateItem(
            Authentication auth,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest req) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, req));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    public ResponseEntity<CartResponse> removeItem(Authentication auth, @PathVariable Long itemId) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }
}
