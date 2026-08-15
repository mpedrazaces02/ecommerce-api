package com.miguelpedraza.ecommerce.product.presentation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "name is required")
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        @NotNull(message = "price is required")
        @Positive(message = "price must be positive")
        BigDecimal price,
        @NotBlank(message = "sku is required")
        @Size(max = 100)
        String sku,
        @Size(max = 255)
        String category
) {}
