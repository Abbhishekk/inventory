package com.inventoryManagement.product_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private  Long id;
    private String name;
    private int quantity;
    private int reorderLevel;
    private CategoryDto category;
    private SupplierDto supplier;
}
