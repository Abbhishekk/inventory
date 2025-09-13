package com.inventoryManagement.supplier_service.repository;

import com.inventoryManagement.supplier_service.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepo extends JpaRepository<Supplier, Long> {
}
