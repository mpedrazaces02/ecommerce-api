package com.miguelpedraza.ecommerce.product.application.service;

import com.miguelpedraza.ecommerce.product.application.exception.ProductAlreadyExistsException;
import com.miguelpedraza.ecommerce.product.domain.Product;
import com.miguelpedraza.ecommerce.product.infrastructure.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private ProductJpaRepository repository;
    private ProductService service;
    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductJpaRepository.class);
        service = new ProductService(repository);
    }
    @Test
    void createProduct_success() {
        when(repository.findBySku("SKU1")).thenReturn(Optional.empty());
        when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        Product p = service.createProduct("Name", "Desc", BigDecimal.valueOf(10.5), "SKU1", "cat");
        assertThat(p.getName()).isEqualTo("Name");
        verify(repository).save(any(Product.class));
    }
    @Test
    void createProduct_duplicateSku_throws() {
        when(repository.findBySku("SKU1")).thenReturn(Optional.of(new Product("x","x",BigDecimal.ONE,"SKU1","c")));
        assertThrows(ProductAlreadyExistsException.class, () -> service.createProduct("Name","D",BigDecimal.TEN,"SKU1","c"));
    }
}
