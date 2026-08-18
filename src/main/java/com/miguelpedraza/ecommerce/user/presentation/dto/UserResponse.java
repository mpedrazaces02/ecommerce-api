package com.miguelpedraza.ecommerce.user.presentation.dto;

import java.time.Instant;

public record UserResponse(Long id, String name, String email, boolean active, Instant createdAt, Instant updatedAt) {}
