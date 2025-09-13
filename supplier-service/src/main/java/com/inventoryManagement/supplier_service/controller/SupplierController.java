package com.inventoryManagement.supplier_service.controller;

import com.inventoryManagement.supplier_service.dto.SupplierRequest;
import com.inventoryManagement.supplier_service.dto.SupplierResponse;
import com.inventoryManagement.supplier_service.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@RequestBody SupplierRequest supplierRequest) {
        SupplierResponse newSupplier = supplierService.createSupplier(supplierRequest);

        // Build the URI for the newly created resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newSupplier.getId())
                .toUri();

        // Return 201 Created with the Location header
        return ResponseEntity.created(location).body(newSupplier);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        try {
            SupplierResponse supplier = supplierService.getSupplierById(id);
            return ResponseEntity.ok(supplier);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Returns 404 Not Found
        }
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllCategories();
        if (suppliers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 No Content for an empty list
        }
        return ResponseEntity.ok(suppliers); // Returns 200 OK with the list
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequest supplierRequest) {
        supplierService.updateSupplier(id, supplierRequest);
        return ResponseEntity.noContent().build(); // Returns 204 No Content for a successful update
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content for a successful deletion
    }
}
