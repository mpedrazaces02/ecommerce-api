package com.miguelpedraza.ecommerce.order.presentation.mapper;

import com.miguelpedraza.ecommerce.order.domain.Order;
import com.miguelpedraza.ecommerce.order.domain.OrderItem;
import com.miguelpedraza.ecommerce.order.presentation.dto.OrderItemResponse;
import com.miguelpedraza.ecommerce.order.presentation.dto.OrderResponse;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderMapper::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                items,
                order.getCreatedAt()
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getSku(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }
}
