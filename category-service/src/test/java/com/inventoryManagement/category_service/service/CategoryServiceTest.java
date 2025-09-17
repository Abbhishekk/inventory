package com.inventoryManagement.category_service.service;

import com.inventoryManagement.category_service.dto.CategoryRequest;
import com.inventoryManagement.category_service.dto.CategoryResponse;
import com.inventoryManagement.category_service.model.Category;
import com.inventoryManagement.category_service.repository.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    private CategoryRequest request;

    @BeforeEach
    void setUp() {
        request = CategoryRequest.builder()
                .name("Electronics")
                .description("Devices and gadgets")
                .build();
    }

    @Test
    void createCategory_savesAndReturnsResponse() {
        Category saved = Category.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .build();

        when(categoryRepo.save(any(Category.class))).thenReturn(saved);

        CategoryResponse response = categoryService.createCategory(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Electronics");
        assertThat(response.getDescription()).isEqualTo("Devices and gadgets");

        verify(categoryRepo).save(categoryCaptor.capture());
        Category captured = categoryCaptor.getValue();
        assertThat(captured.getId()).isNull(); // before save id should be null
        assertThat(captured.getName()).isEqualTo(request.getName());
    }

    @Test
    void getAllCategories_mapsList() {
        Category c1 = Category.builder().id(1L).name("A").description("a").build();
        Category c2 = Category.builder().id(2L).name("B").description("b").build();

        when(categoryRepo.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    void mapToCategoryResponse_mapsCorrectly() {
        Category c = Category.builder().id(5L).name("X").description("desc").build();
        CategoryResponse r = categoryService.mapToCategoryResponse(c);

        assertThat(r.getId()).isEqualTo(5L);
        assertThat(r.getName()).isEqualTo("X");
        assertThat(r.getDescription()).isEqualTo("desc");
    }

    @Test
    void getCategoryById_whenPresent_returnsResponse() {
        Category c = Category.builder().id(7L).name("Z").description("zz").build();
        when(categoryRepo.findById(7L)).thenReturn(Optional.of(c));

        CategoryResponse r = categoryService.getCategoryById(7L);

        assertThat(r.getId()).isEqualTo(7L);
        assertThat(r.getName()).isEqualTo("Z");
    }

    @Test
    void updateCategory_whenPresent_savesUpdated() {
        Category existing = Category.builder().id(3L).name("Old").description("old").build();
        when(categoryRepo.findById(3L)).thenReturn(Optional.of(existing));

        CategoryRequest updateReq = CategoryRequest.builder().name("New").description("new").build();

        categoryService.updateCategory(3L, updateReq);

        verify(categoryRepo).save(categoryCaptor.capture());
        Category saved = categoryCaptor.getValue();
        assertThat(saved.getId()).isEqualTo(3L);
        assertThat(saved.getName()).isEqualTo("New");
        assertThat(saved.getDescription()).isEqualTo("new");
    }

    @Test
    void updateCategory_whenAbsent_throws() {
        when(categoryRepo.findById(100L)).thenReturn(Optional.empty());

        CategoryRequest updateReq = CategoryRequest.builder().name("x").description("y").build();

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(100L, updateReq));
    }

    @Test
    void deleteCategory_callsRepository() {
        doNothing().when(categoryRepo).deleteById(42L);
        categoryService.deleteCategory(42L);
        verify(categoryRepo).deleteById(42L);
    }
}
