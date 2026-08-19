package com.miguelpedraza.ecommerce.cart.presentation.controller;

import com.miguelpedraza.ecommerce.cart.application.service.CartService;
import com.miguelpedraza.ecommerce.cart.presentation.dto.AddCartItemRequest;
import com.miguelpedraza.ecommerce.cart.presentation.dto.CartResponse;
import com.miguelpedraza.ecommerce.cart.presentation.dto.UpdateCartItemRequest;
import com.miguelpedraza.ecommerce.cart.presentation.mapper.CartMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@Validated
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable Long userId) {
        var cart = service.getCart(userId);
        return CartMapper.toResponse(cart);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(@PathVariable Long userId, @Valid @RequestBody AddCartItemRequest req) {
        var cart = service.addItem(userId, req.productId(), req.quantity());
        return ResponseEntity.ok(CartMapper.toResponse(cart));
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long userId, @PathVariable Long productId, @Valid @RequestBody UpdateCartItemRequest req) {
        var cart = service.updateItemQuantity(userId, productId, req.quantity());
        return ResponseEntity.ok(CartMapper.toResponse(cart));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        service.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long userId) {
        service.clearCart(userId);
    }
}
