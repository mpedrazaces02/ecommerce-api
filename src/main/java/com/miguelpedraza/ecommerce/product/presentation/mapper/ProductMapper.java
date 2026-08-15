package com.miguelpedraza.ecommerce.product.presentation.mapper;

import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.product.presentation.dto.CreateProductRequest;
import com.miguelpedraza.ecommerce.product.presentation.dto.ProductResponse;
import com.miguelpedraza.ecommerce.product.presentation.dto.UpdateProductRequest;

public final class ProductMapper {
    private ProductMapper() {}
        public static Product toEntity(CreateProductRequest r) {
        return new Product(r.name(), r.description(), r.price(), r.sku(), r.category());
    }
        public static ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getSku(), p.getCategory(), p.isActive(), p.getCreatedAt(), p.getUpdatedAt());
    }
        public static void applyUpdate(Product p, UpdateProductRequest r) {
        p.update(r.name(), r.description(), r.price(), r.sku(), r.category(), r.active());
    }
}
