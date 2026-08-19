package com.miguelpedraza.ecommerce.order.application.service;

import com.miguelpedraza.ecommerce.cart.application.exception.CartNotFoundException;
import com.miguelpedraza.ecommerce.cart.domain.Cart;
import com.miguelpedraza.ecommerce.cart.infrastructure.repository.CartJpaRepository;
import com.miguelpedraza.ecommerce.order.application.exception.OrderNotFoundException;
import com.miguelpedraza.ecommerce.order.domain.Order;
import com.miguelpedraza.ecommerce.order.infrastructure.repository.OrderJpaRepository;
import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderJpaRepository orderRepository;
    private CartJpaRepository cartRepository;
    private OrderService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderJpaRepository.class);
        cartRepository = mock(CartJpaRepository.class);
        service = new OrderService(orderRepository, cartRepository);
    }

    @Test
    void createOrderFromCart_success() {
        User user = createUser(1L);
        Product product = createProduct(10L, "SKU-1", new BigDecimal("9.99"));
        Cart cart = new Cart(user);
        cart.addItem(product, 2);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 99L);
            return order;
        });

        Order created = service.createOrderFromCart(1L);

        assertThat(created.getUser().getId()).isEqualTo(1L);
        assertThat(created.getItems()).hasSize(1);
        assertThat(created.getTotalAmount()).isEqualByComparingTo(new BigDecimal("19.98"));
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void createOrderFromCart_cartEmpty_throws() {
        User user = createUser(1L);
        Cart cart = new Cart(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(IllegalStateException.class, () -> service.createOrderFromCart(1L));
    }

    @Test
    void listOrders_returnsPage() {
        User user = createUser(1L);
        Order order = new Order(user);
        order.addItem(createProduct(10L, "SKU-2", new BigDecimal("5.00")), 1);
        when(orderRepository.findByUserId(eq(1L), any())).thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));

        var page = service.listOrders(1L, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void cancelOrder_success() {
        User user = createUser(1L);
        Order order = new Order(user);
        order.addItem(createProduct(10L, "SKU-3", new BigDecimal("10.00")), 1);
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order canceled = service.cancelOrder(5L);

        assertThat(canceled.getStatus().name()).isEqualTo("CANCELLED");
    }

    @Test
    void getOrder_notFound_throws() {
        when(orderRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.getOrder(404L));
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
