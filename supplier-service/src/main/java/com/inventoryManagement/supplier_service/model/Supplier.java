package com.inventoryManagement.supplier_service.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "supplier_name")
    private String name;

    @Column(name = "contact_no")
    private Long contactNo;

    @Column(name = "address")
    private String address;
}
