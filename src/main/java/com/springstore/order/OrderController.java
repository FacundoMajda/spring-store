package com.springstore.order;

import com.springstore.order.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Checkout", description = "Converts the current cart into an order and clears the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Cart is empty"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    public ResponseEntity<OrderResponse> checkout(Authentication auth) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(userId));
    }

    @GetMapping
    @Operation(summary = "List user orders", description = "Returns a paginated list of orders for the authenticated user")
    public ResponseEntity<Page<OrderResponse>> findAll(Authentication auth, @ParameterObject Pageable pageable) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(orderService.findAllByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns a specific order. Users can only see their own orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> findById(Authentication auth, @PathVariable Long id) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(orderService.findById(id, userId));
    }
}
