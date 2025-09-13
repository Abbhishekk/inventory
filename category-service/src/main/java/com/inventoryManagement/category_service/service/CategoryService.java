package com.inventoryManagement.category_service.service;

import com.inventoryManagement.category_service.dto.CategoryRequest;
import com.inventoryManagement.category_service.dto.CategoryResponse;
import com.inventoryManagement.category_service.model.Category;
import com.inventoryManagement.category_service.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryResponse createCategory(CategoryRequest categoryRequest){
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
        Category cat= categoryRepo.save(category);
        return CategoryResponse.builder()
                .name(cat.getName())
                .description(cat.getDescription())
                .id(cat.getId())
                .build();
    }

    public List<CategoryResponse> getAllCategories(){
        List<Category> allcat = categoryRepo.findAll();
        return allcat.stream().map(this::mapToCategoryResponse).toList();
    }
    
    public CategoryResponse mapToCategoryResponse(Category cat){
        return CategoryResponse.builder()
                .name(cat.getName())
                .id(cat.getId())
                .description(cat.getDescription())
                .build();
    }
    public CategoryResponse getCategoryById(Long id){
        Optional<Category> getCatById = categoryRepo.findById(id);
        return CategoryResponse.builder()
                .description(getCatById.get().getDescription())
                .name(getCatById.get().getName())
                .id(getCatById.get().getId()).build();
    }

    public void updateCategory(Long id,CategoryRequest categoryRequest){
        Optional<Category> productFound = categoryRepo.findById(id);

        if (productFound.isPresent()) {
            Category existingProduct = getExistingCategory(categoryRequest, productFound);

            categoryRepo.save(existingProduct);
        } else {
            throw new RuntimeException("Product with ID " + id + " not found.");
        }
    }

    private static Category getExistingCategory(CategoryRequest prod, Optional<Category> categoryFound) {
        Category existingProduct = categoryFound.get();

        existingProduct.setName(prod.getName());
        existingProduct.setDescription(prod.getDescription());
        return existingProduct;
    }

    public void deleteCategory(Long id){
        categoryRepo.deleteById(id);
    }
    


}
