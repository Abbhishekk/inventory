package com.inventoryManagement.category_service.repository;

import com.inventoryManagement.category_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
