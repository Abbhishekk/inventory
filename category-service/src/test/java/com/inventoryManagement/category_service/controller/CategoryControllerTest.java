package com.inventoryManagement.category_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryManagement.category_service.dto.CategoryRequest;
import com.inventoryManagement.category_service.dto.CategoryResponse;
import com.inventoryManagement.category_service.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_returnsCreatedWithLocation() throws Exception {
        CategoryResponse response = CategoryResponse.builder().id(10L).name("N").description("d").build();
        CategoryRequest req = CategoryRequest.builder().name("N").description("d").build();

        Mockito.when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/category/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getCategoryById_found_returnsOk() throws Exception {
        CategoryResponse response = CategoryResponse.builder().id(5L).name("X").description("d").build();
        Mockito.when(categoryService.getCategoryById(5L)).thenReturn(response);

        mockMvc.perform(get("/api/category/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("X"));
    }

    @Test
    void getCategoryById_notFound_returns404() throws Exception {
        Mockito.when(categoryService.getCategoryById(99L)).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/api/category/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategories_noContent_whenEmpty() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllCategories_returnsList_whenNotEmpty() throws Exception {
        CategoryResponse a = CategoryResponse.builder().id(1L).name("A").description("a").build();
        CategoryResponse b = CategoryResponse.builder().id(2L).name("B").description("b").build();
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateCategory_returnsNoContent() throws Exception {
        CategoryRequest req = CategoryRequest.builder().name("u").description("u").build();

        Mockito.doNothing().when(categoryService).updateCategory(eq(2L), any(CategoryRequest.class));

        mockMvc.perform(patch("/api/category/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_returnsNoContent() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(3L);

        mockMvc.perform(delete("/api/category/3"))
                .andExpect(status().isNoContent());
    }
}
