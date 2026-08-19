package com.miguelpedraza.ecommerce.cart.application.service;

import com.miguelpedraza.ecommerce.cart.application.exception.CartItemNotFoundException;
import com.miguelpedraza.ecommerce.cart.application.exception.CartNotFoundException;
import com.miguelpedraza.ecommerce.cart.domain.Cart;
import com.miguelpedraza.ecommerce.cart.infrastructure.repository.CartJpaRepository;
import com.miguelpedraza.ecommerce.product.application.exception.ProductNotFoundException;
import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.product.infrastructure.repository.ProductJpaRepository;
import com.miguelpedraza.ecommerce.user.application.exception.UserNotFoundException;
import com.miguelpedraza.ecommerce.user.domain.User;
import com.miguelpedraza.ecommerce.user.infrastructure.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    private final CartJpaRepository cartRepository;
    private final UserJpaRepository userRepository;
    private final ProductJpaRepository productRepository;

    public CartService(CartJpaRepository cartRepository, UserJpaRepository userRepository, ProductJpaRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId));
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }

    public Cart addItem(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Cart cart = getOrCreateCart(userId);
        cart.addItem(product, quantity);
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        if (cart.getItems().stream().noneMatch(item -> item.getProduct().getId().equals(productId))) {
            throw new CartItemNotFoundException(productId);
        }

        cart.updateItemQuantity(productId, quantity);
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        if (cart.getItems().stream().noneMatch(item -> item.getProduct().getId().equals(productId))) {
            throw new CartItemNotFoundException(productId);
        }

        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        cart.clear();
        cartRepository.save(cart);
    }
}
