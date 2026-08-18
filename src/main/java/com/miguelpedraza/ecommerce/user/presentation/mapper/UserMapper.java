package com.miguelpedraza.ecommerce.user.presentation.mapper;

import com.miguelpedraza.ecommerce.user.domain.User;
import com.miguelpedraza.ecommerce.user.presentation.dto.UserResponse;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.isActive(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
