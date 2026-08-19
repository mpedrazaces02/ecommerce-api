package com.miguelpedraza.ecommerce.order.application.service;

import com.miguelpedraza.ecommerce.cart.application.exception.CartNotFoundException;
import com.miguelpedraza.ecommerce.cart.domain.Cart;
import com.miguelpedraza.ecommerce.cart.domain.CartItem;
import com.miguelpedraza.ecommerce.cart.infrastructure.repository.CartJpaRepository;
import com.miguelpedraza.ecommerce.order.application.exception.OrderNotFoundException;
import com.miguelpedraza.ecommerce.order.domain.Order;
import com.miguelpedraza.ecommerce.order.infrastructure.repository.OrderJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderJpaRepository orderRepository;
    private final CartJpaRepository cartRepository;

    public OrderService(OrderJpaRepository orderRepository, CartJpaRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public Order createOrderFromCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        for (CartItem item : cart.getItems()) {
            if (item.getProduct() == null) {
                throw new IllegalStateException("Cart contains a product that no longer exists");
            }
            if (!item.getProduct().isActive()) {
                throw new IllegalStateException("Product " + item.getProduct().getId() + " is unavailable");
            }
        }

        Order order = new Order(cart.getUser());
        for (CartItem item : cart.getItems()) {
            order.addItem(item.getProduct(), item.getQuantity());
        }

        Order savedOrder = orderRepository.save(order);
        cart.clear();
        cartRepository.save(cart);
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional(readOnly = true)
    public Page<Order> listOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Order cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        return orderRepository.save(order);
    }
}
