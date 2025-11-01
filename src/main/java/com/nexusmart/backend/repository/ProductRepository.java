package com.nexusmart.backend.repository;

import com.nexusmart.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find product by SKU
    Optional<Product> findBySku(String sku);

    // Find all active products
    List<Product> findByActiveTrue();

    // Find products by name containing (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products with low stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // Check if SKU exists (for validation)
    boolean existsBySku(String sku);
}