package com.inventoryManagement.product_service.service;

import com.inventoryManagement.product_service.dto.CategoryDto;
import com.inventoryManagement.product_service.dto.ProductOnlyResponse;
import com.inventoryManagement.product_service.dto.ProductRequest;
import com.inventoryManagement.product_service.dto.ProductResponse;
import com.inventoryManagement.product_service.dto.SupplierDto;
import com.inventoryManagement.product_service.model.Product;
import com.inventoryManagement.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    ProductRepository repo;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ProductService service;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_savesAndReturns() {
        ProductRequest req = ProductRequest.builder()
                .name("Widget")
                .quantity(10)
                .reorderLevel(5)
                .category(1L)
                .supplier(2L)
                .build();

        Product saved = Product.builder().id(100L).name("Widget").quantity(10).reorderLevel(5).category(1L).supplier(2L).build();
        when(repo.save(any())).thenReturn(saved);

        Product out = service.addProduct(req);

        verify(repo).save(productCaptor.capture());
        Product captured = productCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("Widget");
        assertThat(out.getId()).isEqualTo(100L);
    }

    @Test
    void getProductById_returnsMappedResponse() {
        Product p = Product.builder().id(1L).name("X").quantity(3).reorderLevel(2).category(10L).supplier(20L).build();
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        CategoryDto cat = CategoryDto.builder().id(10L).name("Cat").description("d").build();
        SupplierDto sup = SupplierDto.builder().id(20L).name("Sup").contactNo(123L).address("a").build();

        when(restTemplate.exchange(contains("/10"), any(), any(), eq(CategoryDto.class)))
                .thenReturn(new ResponseEntity<>(cat, HttpStatus.OK));
        when(restTemplate.exchange(contains("/20"), any(), any(), eq(SupplierDto.class)))
                .thenReturn(new ResponseEntity<>(sup, HttpStatus.OK));

        Optional<ProductResponse> resp = service.getProductById(1L);
        assertThat(resp).isPresent();
        ProductResponse r = resp.get();
        assertThat(r.getCategory().getName()).isEqualTo("Cat");
        assertThat(r.getSupplier().getName()).isEqualTo("Sup");
    }

    @Test
    void getLowStockProducts_filtersCorrectly() {
        Product a = Product.builder().id(1L).name("A").quantity(2).reorderLevel(5).build();
        Product b = Product.builder().id(2L).name("B").quantity(10).reorderLevel(5).build();
        when(repo.findAll()).thenReturn(Arrays.asList(a, b));

        List<ProductOnlyResponse> low = service.getLowStockProducts();
        assertThat(low).hasSize(1);
        assertThat(low.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getAllProducts_mapsAll() {
        Product p1 = Product.builder().id(1L).name("P1").quantity(1).reorderLevel(1).category(5L).supplier(6L).build();
        when(repo.findAll()).thenReturn(Arrays.asList(p1));

        when(restTemplate.exchange(contains("/5"), any(), any(), eq(CategoryDto.class)))
                .thenReturn(new ResponseEntity<>(CategoryDto.builder().id(5L).name("C").build(), HttpStatus.OK));
        when(restTemplate.exchange(contains("/6"), any(), any(), eq(SupplierDto.class)))
                .thenReturn(new ResponseEntity<>(SupplierDto.builder().id(6L).name("S").build(), HttpStatus.OK));

        List<ProductResponse> all = service.getAllProducts();
        assertThat(all).hasSize(1);
    }

    @Test
    void updateProductDetail_updatesWhenFound() {
        Product existing = Product.builder().id(50L).name("Old").quantity(1).reorderLevel(1).category(1L).supplier(1L).build();
        when(repo.findById(50L)).thenReturn(Optional.of(existing));

        ProductRequest req = ProductRequest.builder().name("New").quantity(5).reorderLevel(2).category(2L).supplier(3L).build();

        service.updateProductDetail(50L, req);

        verify(repo).save(any(Product.class));
        assertThat(existing.getName()).isEqualTo("New");
    }

    @Test
    void updateProductDetail_throwsWhenMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        try {
            service.updateProductDetail(999L, ProductRequest.builder().build());
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).contains("not found");
        }
    }

    @Test
    void deleteProductDetails_callsRepo() {
        doNothing().when(repo).deleteById(7L);
        service.deleteProductDetails(7L);
        verify(repo).deleteById(7L);
    }
}
