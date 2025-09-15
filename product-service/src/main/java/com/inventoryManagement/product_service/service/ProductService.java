package com.inventoryManagement.product_service.service;

import com.inventoryManagement.product_service.dto.*;
import com.inventoryManagement.product_service.model.Product;
import com.inventoryManagement.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    public List<ProductOnlyResponse> getLowStockProducts() {
        List<Product> products = productRepo.findAll()
                .stream()
                .filter(p -> p.getQuantity() <= p.getReorderLevel())
                .toList();

        return products.stream()
                .map(p->ProductOnlyResponse.builder().id(p.getId()).name(p.getName()).reorderLevel(p.getReorderLevel()).quantity(p.getQuantity()).build())
                .toList();
    }


    private ProductResponse fetchAndMapToProductResponse(Product product) {

        // 1. Extract JWT from current request
        String jwtToken = RequestContextHolder.getRequestAttributes() != null
                ? ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization")
                : null;

        HttpHeaders headers = new HttpHeaders();
        if (jwtToken != null) {
            headers.set("Authorization", jwtToken); // pass along Bearer token
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. Call Category Service
        ResponseEntity<CategoryDto> categoryResponse = restTemplate.exchange(
                categoryServiceUrl + "/" + product.getCategory(),
                HttpMethod.GET,
                entity,
                CategoryDto.class
        );

        // 3. Call Supplier Service
        ResponseEntity<SupplierDto> supplierResponse = restTemplate.exchange(
                supplierServiceUrl + "/" + product.getSupplier(),
                HttpMethod.GET,
                entity,
                SupplierDto.class
        );

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .reorderLevel(product.getReorderLevel())
                .category(categoryResponse.getBody())
                .supplier(supplierResponse.getBody())
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

