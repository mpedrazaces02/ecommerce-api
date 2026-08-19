package com.miguelpedraza.ecommerce.cart.domain;

import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected Cart() {
    }

    public Cart(User user) {
        this.user = user;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public List<CartItem> getItems() { return items; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        for (CartItem item : items) {
            if (Objects.equals(item.getProduct().getId(), product.getId())) {
                item.addQuantity(quantity);
                return;
            }
        }

        items.add(new CartItem(this, product, quantity));
    }

    public void updateItemQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        for (CartItem item : items) {
            if (Objects.equals(item.getProduct().getId(), productId)) {
                item.setQuantity(quantity);
                return;
            }
        }

        throw new IllegalArgumentException("Product not found in cart: " + productId);
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> Objects.equals(item.getProduct().getId(), productId));
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
