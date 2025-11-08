package com.nexusmart.backend.service;

import com.nexusmart.backend.dto.CreateProductRequest;
import com.nexusmart.backend.dto.ProductDTO;
import com.nexusmart.backend.model.Product;
import com.nexusmart.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<ProductDTO> getAllProducts() {
        logger.debug("Retrieving all products from repository");
        try {
            List<Product> products = productRepository.findAll();
            logger.debug("Retrieved {} raw product entities", products.size());
            return products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve all products from repository", e);
            throw e;
        }
    }

    public ProductDTO getProductById(Long id) {
        logger.debug("Looking up product by ID: {}", id);
        try {
            Product product = productRepository.findById(id)
                    .orElse(null);
            if (product == null) {
                logger.debug("Product not found in repository for ID: {}", id);
                return null;
            }
            logger.debug("Found product in repository: ID={}, Name={}", id, product.getName());
            return convertToDTO(product);
        } catch (Exception e) {
            logger.error("Error looking up product by ID: {}", id, e);
            throw e;
        }
    }

    public ProductDTO createProduct(CreateProductRequest request) {
        logger.debug("Creating new product entity with name: {}", request.getName());
        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setSku(request.getSku());
            product.setActive(request.getActive() != null ? request.getActive() : true);

            Product savedProduct = productRepository.save(product);
            logger.debug("Product entity saved successfully with generated ID: {}", savedProduct.getId());
            return convertToDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Failed to create product with name: {}", request.getName(), e);
            throw e;
        }
    }

    public ProductDTO updateProduct(Long id, CreateProductRequest request) {
        logger.debug("Updating product entity with ID: {}", id);
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Product not found for update - ID: {}", id);
                        return new RuntimeException("Product not found with id: " + id);
                    });

            logger.debug("Updating product fields - old name: {}, new name: {}",
                    existingProduct.getName(), request.getName());

            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice(request.getPrice());
            existingProduct.setStockQuantity(request.getStockQuantity());
            existingProduct.setSku(request.getSku());
            if (request.getActive() != null) {
                existingProduct.setActive(request.getActive());
            }

            Product updatedProduct = productRepository.save(existingProduct);
            logger.debug("Product entity updated successfully - ID: {}", id);
            return convertToDTO(updatedProduct);
        } catch (Exception e) {
            logger.error("Failed to update product with ID: {}", id, e);
            throw e;
        }
    }

    public void deleteProduct(Long id) {
        logger.debug("Deleting product entity with ID: {}", id);
        try {
            if (!productRepository.existsById(id)) {
                logger.warn("Product not found for deletion - ID: {}", id);
                throw new RuntimeException("Product not found with id: " + id);
            }
            productRepository.deleteById(id);
            logger.debug("Product entity deleted successfully - ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete product with ID: {}", id, e);
            throw e;
        }
    }

    public List<ProductDTO> searchProducts(String name) {
        logger.debug("Searching products in repository with name pattern: '{}'", name);
        try {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
            logger.debug("Repository search found {} entities for pattern: '{}'", products.size(), name);
            return products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to search products with name pattern: '{}'", name, e);
            throw e;
        }
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .build();
    }
}