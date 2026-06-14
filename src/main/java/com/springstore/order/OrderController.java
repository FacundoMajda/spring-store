package com.springstore.order;

import com.springstore.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(Authentication auth) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll(Authentication auth) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(orderService.findAllByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(Authentication auth, @PathVariable Long id) {
        var userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(orderService.findById(id, userId));
    }
}
