package com.miguelpedraza.ecommerce.product.application.service;

import com.miguelpedraza.ecommerce.product.application.exception.ProductAlreadyExistsException;
import com.miguelpedraza.ecommerce.product.application.exception.ProductNotFoundException;
import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.product.infrastructure.repository.ProductJpaRepository;
import com.miguelpedraza.ecommerce.product.infrastructure.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductJpaRepository repository;
        public ProductService(ProductJpaRepository repository) {
        this.repository = repository;
    }
        public Product createProduct(String name, String description, BigDecimal price, String sku, String category) {
        Optional<Product> existing = repository.findBySku(sku);
        if (existing.isPresent()) throw new ProductAlreadyExistsException(sku);
        Product p = new Product(name, description, price, sku, category);
        return repository.save(p);
    }
        @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
        @Transactional(readOnly = true)
    public Page<Product> listProducts(String name, String sku, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.nameContains(name))
                .and(ProductSpecification.skuEquals(sku))
                .and(ProductSpecification.categoryEquals(category))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice));
        return repository.findAll(spec, pageable);
    }
        public Product updateProduct(Long id, String name, String description, BigDecimal price, String sku, String category, boolean active) {
        Product p = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        // If SKU changed, ensure uniqueness
        if (!p.getSku().equals(sku)) {
            repository.findBySku(sku).ifPresent(existing -> { throw new ProductAlreadyExistsException(sku); });
        }
        p.update(name, description, price, sku, category, active);
        return repository.save(p);
    }
        public void deactivateProduct(Long id) {
        Product p = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        p.deactivate();
        repository.save(p);
    }
}
