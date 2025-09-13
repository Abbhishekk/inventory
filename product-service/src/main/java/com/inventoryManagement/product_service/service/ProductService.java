package com.inventoryManagement.product_service.service;

import com.inventoryManagement.product_service.dto.CategoryDto;
import com.inventoryManagement.product_service.dto.ProductRequest;
import com.inventoryManagement.product_service.dto.ProductResponse;
import com.inventoryManagement.product_service.dto.SupplierDto;
import com.inventoryManagement.product_service.model.Product;
import com.inventoryManagement.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final RestTemplate restTemplate;

    @Value("${category.service.url}")
    private String categoryServiceUrl;

    @Value("${supplier.service.url}")
    private String supplierServiceUrl;

    public Product addProduct(ProductRequest prod){
        Product product = Product.builder()
                .name(prod.getName())
                .quantity(prod.getQuantity())
                .reorderLevel(prod.getReorderLevel())
                .category(prod.getCategory())
                .supplier(prod.getSupplier()).build();
        return productRepo.save(product);
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepo.findById(id)
                .map(this::fetchAndMapToProductResponse);
    }

    private ProductResponse fetchAndMapToProductResponse(Product product) {

        CategoryDto category = restTemplate.getForObject(
                categoryServiceUrl + "/" + product.getCategory(), CategoryDto.class);
        SupplierDto supplier = restTemplate.getForObject(
                supplierServiceUrl + "/" + product.getSupplier(), SupplierDto.class);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .reorderLevel(product.getReorderLevel())
                .category(category)
                .supplier(supplier)
                .build();
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepo.findAll();
        return products.stream()
                .map(this::fetchAndMapToProductResponse)
                .toList();
    }

    @Transactional
    public void updateProductDetail(Long id, ProductRequest prod) {

        Optional<Product> productFound = productRepo.findById(id);

        if (productFound.isPresent()) {
            Product existingProduct = getExistingProduct(prod, productFound);

            productRepo.save(existingProduct);
        } else {
            throw new RuntimeException("Product with ID " + id + " not found.");
        }
    }

    private static Product getExistingProduct(ProductRequest prod, Optional<Product> productFound) {
        Product existingProduct = productFound.get();

        existingProduct.setName(prod.getName());
        existingProduct.setQuantity(prod.getQuantity());
        existingProduct.setReorderLevel(prod.getReorderLevel());

        existingProduct.setCategory(prod.getCategory());
        existingProduct.setSupplier(prod.getSupplier());
        return existingProduct;
    }

    public void deleteProductDetails(Long id){
        productRepo.deleteById(id);
    }
}

