package com.inventoryManagement.product_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupplierDto {
    private Long id;
    private String name;
    private Long contactNo;
    private String address;
}
