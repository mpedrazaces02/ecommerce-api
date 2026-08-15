package com.miguelpedraza.ecommerce.product.presentation;

import com.miguelpedraza.ecommerce.product.application.service.ProductService;
import com.miguelpedraza.ecommerce.product.presentation.dto.CreateProductRequest;
import com.miguelpedraza.ecommerce.product.presentation.dto.ProductResponse;
import com.miguelpedraza.ecommerce.product.presentation.dto.UpdateProductRequest;
import com.miguelpedraza.ecommerce.product.presentation.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request, UriComponentsBuilder uriBuilder) {
        var created = service.createProduct(request.name(), request.description(), request.price(), request.sku(), request.category());
        ProductResponse resp = ProductMapper.toResponse(created);
        URI location = uriBuilder.path("/api/v1/products/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.listProducts(name, sku, category, minPrice, maxPrice, pageable).map(ProductMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return ProductMapper.toResponse(service.getProduct(id));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        var updated = service.updateProduct(id, request.name(), request.description(), request.price(), request.sku(), request.category(), request.active());
        return ProductMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deactivateProduct(id);
    }
}
