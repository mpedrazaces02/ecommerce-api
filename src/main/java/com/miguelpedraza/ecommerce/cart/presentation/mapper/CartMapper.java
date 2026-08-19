package com.miguelpedraza.ecommerce.cart.presentation.mapper;

import com.miguelpedraza.ecommerce.cart.domain.Cart;
import com.miguelpedraza.ecommerce.cart.domain.CartItem;
import com.miguelpedraza.ecommerce.cart.presentation.dto.CartItemResponse;
import com.miguelpedraza.ecommerce.cart.presentation.dto.CartResponse;

import java.util.List;

public final class CartMapper {

    private CartMapper() {}

    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartMapper::toItemResponse)
                .toList();

        return new CartResponse(cart.getId(), cart.getUser().getId(), items, cart.getTotalAmount());
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getSku(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }
}
