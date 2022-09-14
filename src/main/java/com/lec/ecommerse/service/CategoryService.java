package com.lec.ecommerse.service;

import com.lec.ecommerse.domain.Category;
import com.lec.ecommerse.exception.ConflictException;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createCategory(Category category) {
        boolean existsByCategoryName = categoryRepository.existsByCategoryName(category.getCategoryName());
        if (existsByCategoryName){
            throw new ConflictException("Category is already exists!");
        }
        categoryRepository.save(category);
    }

    public List<Category> listCategory() {
        List<Category> all = categoryRepository.findAll();

        return all;
    }

    public void editCategory(int categoryId, Category updateCategory) {
        boolean existsById = categoryRepository.existsById(categoryId);
        if (!existsById){
            throw new ResourceNotFoundException("Category does not exist !");
        }
        Category category = categoryRepository.getById(categoryId);
        category.setCategoryName(updateCategory.getCategoryName());
        category.setDescription(updateCategory.getDescription());
        category.setImageUrl(updateCategory.getImageUrl());

        categoryRepository.save(category);
    }

    public void removeCategoryById(int categoryId) {
        boolean existsById = categoryRepository.existsById(categoryId);
        if (!existsById){
            throw new ResourceNotFoundException("Category does not exist !");
        }
        categoryRepository.deleteById(categoryId);
    }
}
