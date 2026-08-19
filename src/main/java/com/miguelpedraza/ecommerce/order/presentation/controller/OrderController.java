package com.miguelpedraza.ecommerce.order.presentation.controller;

import com.miguelpedraza.ecommerce.order.application.service.OrderService;
import com.miguelpedraza.ecommerce.order.presentation.dto.OrderResponse;
import com.miguelpedraza.ecommerce.order.presentation.mapper.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Long userId) {
        var order = service.createOrderFromCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toResponse(order));
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return OrderMapper.toResponse(service.getOrder(id));
    }

    @GetMapping("/users/{userId}/orders")
    public Page<OrderResponse> listOrders(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.listOrders(userId, pageable).map(OrderMapper::toResponse);
    }

    @DeleteMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long id) {
        service.cancelOrder(id);
    }
}
