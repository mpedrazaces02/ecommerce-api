package com.miguelpedraza.ecommerce.product.application.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String sku) {
        super("Product with SKU already exists: " + sku);
    }
}
