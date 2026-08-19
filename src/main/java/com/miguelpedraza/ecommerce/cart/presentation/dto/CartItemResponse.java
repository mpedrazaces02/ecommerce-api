package com.miguelpedraza.ecommerce.cart.presentation.dto;

import java.math.BigDecimal;

public record CartItemResponse(Long productId, String productName, String sku, BigDecimal price, int quantity, BigDecimal lineTotal) {}
