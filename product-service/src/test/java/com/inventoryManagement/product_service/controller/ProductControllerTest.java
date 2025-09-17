package com.inventoryManagement.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryManagement.product_service.dto.ProductRequest;
import com.inventoryManagement.product_service.model.Product;
import com.inventoryManagement.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController controller;

    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createProduct_returnsCreated() throws Exception {
        ProductRequest req = ProductRequest.builder().name("n").quantity(1).reorderLevel(1).build();
        Product p = Product.builder().id(1L).name("n").quantity(1).reorderLevel(1).build();
        when(productService.addProduct(org.mockito.ArgumentMatchers.any())).thenReturn(p);

        mockMvc.perform(post("/api/product").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProductById_notFound() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/product/999")).andExpect(status().isNotFound());
    }

    @Test
    void getAllProducts_returnsOk() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());
        mockMvc.perform(get("/api/product")).andExpect(status().isOk());
    }

    @Test
    void updateProduct_returnsNoContent() throws Exception {
        ProductRequest req = ProductRequest.builder().name("n").quantity(1).reorderLevel(1).build();
        mockMvc.perform(patch("/api/product/1").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/product/1")).andExpect(status().isNoContent());
    }
}
