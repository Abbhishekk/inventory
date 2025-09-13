package com.inventoryManagement.product_service.repository;

import com.inventoryManagement.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
