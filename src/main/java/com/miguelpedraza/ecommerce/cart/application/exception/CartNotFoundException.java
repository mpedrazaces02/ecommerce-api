package com.miguelpedraza.ecommerce.cart.application.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(Long userId) {
        super("Cart not found for user: " + userId);
    }
}
