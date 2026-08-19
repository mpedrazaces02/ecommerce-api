package com.miguelpedraza.ecommerce.order.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, Long userId, String status, BigDecimal totalAmount, List<OrderItemResponse> items, Instant createdAt) {}
