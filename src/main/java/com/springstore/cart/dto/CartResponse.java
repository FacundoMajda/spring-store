package com.springstore.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items
) {

    public record CartItemResponse(
            Long id,
            Long productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity
    ) {}
}
