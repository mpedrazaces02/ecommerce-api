package com.miguelpedraza.ecommerce.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "name is required")
        String name,
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,
        boolean active
) {}
