package com.nexusmart.backend.service;

import com.nexusmart.backend.dto.CreateProductRequest;
import com.nexusmart.backend.dto.ProductDTO;
import com.nexusmart.backend.model.Product;
import com.nexusmart.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Convert Entity to DTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSku(product.getSku());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setActive(product.getActive());
        return dto;
    }

    // Convert CreateRequest to Entity
    private Product convertToEntity(CreateProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .stockQuantity(request.getStockQuantity())
                .active(true)
                .build();
    }

    // Get all products
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get product by ID
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    // Create new product
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        try {
            System.out.println("Creating product: " + request.getName()); // Debug line

            // Check if SKU already exists
            if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
                throw new RuntimeException("Product with SKU " + request.getSku() + " already exists");
            }

            Product product = convertToEntity(request);
            System.out.println("Product entity created: " + product); // Debug line

            Product savedProduct = productRepository.save(product);
            System.out.println("Product saved with ID: " + savedProduct.getId()); // Debug line

            return convertToDTO(savedProduct);
        } catch (Exception e) {
            System.err.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to see in Postman
        }
    }
    // Update product
    @Transactional
    public ProductDTO updateProduct(Long id, CreateProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Check if SKU is being changed and conflicts with another product
        if (request.getSku() != null &&
                !request.getSku().equals(existingProduct.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Product with SKU " + request.getSku() + " already exists");
        }

        // Update fields
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setSku(request.getSku());
        existingProduct.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    // Delete product (soft delete)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setActive(false);
        productRepository.save(product);
    }

    // Search products by name
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .filter(Product::getActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}