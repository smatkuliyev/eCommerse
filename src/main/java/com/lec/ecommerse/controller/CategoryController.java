package com.lec.ecommerse.controller;

import com.lec.ecommerse.model.Category;
import com.lec.ecommerse.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> createCategory(@RequestBody Category category){
        categoryService.createCategory(category);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Category has been added successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> listCategory(){
        List<Category> categoryList = categoryService.listCategory();

        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }

    @PutMapping("/update/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> updateCategoryById(@PathVariable int categoryId, @RequestBody Category category) {
        categoryService.editCategory(categoryId, category);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Category has been updated successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteCategoryById(@PathVariable int categoryId) {
        categoryService.removeCategoryById(categoryId);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Category has been deleted successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
