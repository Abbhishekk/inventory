package com.inventoryManagement.supplier_service.service;

import com.inventoryManagement.supplier_service.dto.SupplierRequest;
import com.inventoryManagement.supplier_service.dto.SupplierResponse;
import com.inventoryManagement.supplier_service.model.Supplier;
import com.inventoryManagement.supplier_service.repository.SupplierRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SupplierService {

    private final SupplierRepo supplierRepo;

    public SupplierResponse createSupplier(SupplierRequest supplierRequest){
        Supplier supplier = Supplier.builder()
                .name(supplierRequest.getName())
                .address(supplierRequest.getAddress())
                .contactNo(supplierRequest.getContactNo())
                .build();
        Supplier sup= supplierRepo.save(supplier);
        return SupplierResponse.builder()
                .name(sup.getName())
                .address(sup.getAddress())
                .contactNo(sup.getContactNo())
                .id(sup.getId())
                .build();
    }

    public List<SupplierResponse> getAllCategories(){
        List<Supplier> allcat = supplierRepo.findAll();
        return allcat.stream().map(this::mapToSupplierResponse).toList();
    }

    public SupplierResponse mapToSupplierResponse(Supplier cat){
        return SupplierResponse.builder()
                .name(cat.getName())
                .id(cat.getId())
                .contactNo(cat.getContactNo())
                .address(cat.getAddress())
                .build();
    }
    public SupplierResponse getSupplierById(Long id) {
        Optional<Supplier> getSupById = supplierRepo.findById(id);
        return getSupById.map(this::mapToSupplierResponse)
                .orElseThrow(() -> new RuntimeException("Supplier with ID " + id + " not found."));
    }

    public void updateSupplier(Long id,SupplierRequest categoryRequest){
        Optional<Supplier> supplierFound = supplierRepo.findById(id);

        if (supplierFound.isPresent()) {
            Supplier existingSupplier = getExistingSupplier(categoryRequest, supplierFound);

            supplierRepo.save(existingSupplier);
        } else {
            throw new RuntimeException("Supplier with ID " + id + " not found.");
        }
    }

    private static Supplier getExistingSupplier(SupplierRequest prod, Optional<Supplier> supplierFound) {
        Supplier existingSupplier = supplierFound.get();

        existingSupplier.setName(prod.getName());
        existingSupplier.setAddress(prod.getAddress());
        existingSupplier.setContactNo(prod.getContactNo());
        return existingSupplier;
    }

    public void deleteSupplier(Long id){
        supplierRepo.deleteById(id);
    }
}
