package com.miguelpedraza.ecommerce.cart.application.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Long productId) {
        super("Product not found in cart: " + productId);
    }
}
