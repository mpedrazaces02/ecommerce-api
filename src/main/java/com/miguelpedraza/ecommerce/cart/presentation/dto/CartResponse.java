package com.miguelpedraza.ecommerce.cart.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long id, Long userId, List<CartItemResponse> items, BigDecimal totalAmount) {}
