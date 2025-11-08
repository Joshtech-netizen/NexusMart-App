package com.nexusmart.backend.controller;

import com.nexusmart.backend.dto.CreateProductRequest;
import com.nexusmart.backend.dto.ProductDTO;
import com.nexusmart.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Get all products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        logger.debug("Entering getAllProducts method");
        try {
            List<ProductDTO> products = productService.getAllProducts();
            logger.info("Successfully retrieved {} products", products.size());

            // Debug level for large collections to avoid performance hit in production
            if (logger.isDebugEnabled() && !products.isEmpty()) {
                logger.debug("Products retrieved: {}",
                        products.stream().map(ProductDTO::getName).toList());
            }

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error retrieving all products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        logger.debug("Fetching product with ID: {}", id);
        try {
            ProductDTO product = productService.getProductById(id);

            if (product != null) {
                logger.info("Successfully retrieved product: ID={}, Name={}", id, product.getName());
                return ResponseEntity.ok(product);
            } else {
                logger.warn("Product not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new product
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        logger.info("Creating new product with name: {}", request.getName());
        try {
            ProductDTO createdProduct = productService.createProduct(request);
            logger.info("Product created successfully - ID: {}, Name: {}",
                    createdProduct.getId(), createdProduct.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            logger.error("Error creating product with name: {}", request.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {
        logger.info("Updating product with ID: {}, new name: {}", id, request.getName());
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, request);
            logger.info("Product updated successfully - ID: {}", id);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            logger.error("Error updating product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Attempting to delete product with ID: {}", id);
        try {
            productService.deleteProduct(id);
            logger.info("Product deleted successfully - ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search products
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        logger.debug("Searching products with name pattern: '{}'", name);
        try {
            List<ProductDTO> products = productService.searchProducts(name);
            logger.info("Search completed for '{}' - found {} results", name, products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error searching products with name: '{}'", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}