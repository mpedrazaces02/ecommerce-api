package com.miguelpedraza.ecommerce.cart.infrastructure.repository;

import com.miguelpedraza.ecommerce.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
