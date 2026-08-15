package com.miguelpedraza.ecommerce.product.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String sku,
        String category,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
