package com.miguelpedraza.ecommerce.cart.application.service;

import com.miguelpedraza.ecommerce.cart.application.exception.CartNotFoundException;
import com.miguelpedraza.ecommerce.cart.domain.Cart;
import com.miguelpedraza.ecommerce.cart.infrastructure.repository.CartJpaRepository;
import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.product.infrastructure.repository.ProductJpaRepository;
import com.miguelpedraza.ecommerce.user.domain.User;
import com.miguelpedraza.ecommerce.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartJpaRepository cartRepository;
    private UserJpaRepository userRepository;
    private ProductJpaRepository productRepository;
    private CartService service;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartJpaRepository.class);
        userRepository = mock(UserJpaRepository.class);
        productRepository = mock(ProductJpaRepository.class);
        service = new CartService(cartRepository, userRepository, productRepository);
    }

    @Test
    void getOrCreateCart_whenCartDoesNotExist_createsNewCart() {
        User user = createUser(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = service.getOrCreateCart(1L);

        assertThat(cart).isNotNull();
        assertThat(cart.getUser()).isEqualTo(user);
        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItem_success() {
        User user = createUser(1L);
        Product product = createProduct(10L, "SKU-1", new BigDecimal("9.99"));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = service.addItem(1L, 10L, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getProduct().getId()).isEqualTo(10L);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(2);
        assertThat(cart.getTotalAmount()).isEqualByComparingTo(new BigDecimal("19.98"));
    }

    @Test
    void updateItemQuantity_success() {
        User user = createUser(1L);
        Product product = createProduct(10L, "SKU-2", new BigDecimal("5.00"));
        Cart cart = new Cart(user);
        cart.addItem(product, 1);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart updated = service.updateItemQuantity(1L, 10L, 3);

        assertThat(updated.getItems()).hasSize(1);
        assertThat(updated.getItems().getFirst().getQuantity()).isEqualTo(3);
        assertThat(updated.getTotalAmount()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void removeItem_success() {
        User user = createUser(1L);
        Product product = createProduct(10L, "SKU-3", new BigDecimal("7.50"));
        Cart cart = new Cart(user);
        cart.addItem(product, 1);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart updated = service.removeItem(1L, 10L);

        assertThat(updated.getItems()).isEmpty();
    }

    @Test
    void clearCart_success() {
        User user = createUser(1L);
        Product product = createProduct(10L, "SKU-4", new BigDecimal("8.00"));
        Cart cart = new Cart(user);
        cart.addItem(product, 2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.clearCart(1L);

        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void getCart_whenCartMissing_throwsCartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> service.getCart(1L));
    }

    @Test
    void addItem_invalidQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.addItem(1L, 10L, 0));
        verifyNoInteractions(productRepository, userRepository, cartRepository);
    }

    private User createUser(Long id) {
        User user = new User("Test User", "test@example.com", "password123");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Product createProduct(Long id, String sku, BigDecimal price) {
        Product product = new Product("Test Product", "Description", price, sku, "category-a");
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }
}
