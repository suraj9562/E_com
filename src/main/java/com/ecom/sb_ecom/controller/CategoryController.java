package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.config.AppConstants;
import com.ecom.sb_ecom.payload.CategoryDTO;
import com.ecom.sb_ecom.payload.CategoryResponse;
import com.ecom.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        return new ResponseEntity<>(categoryService.findAll(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @PostMapping("/public/category")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO response = categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/public/category/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable("id") Long id){
        CategoryDTO response = categoryService.deleteCategory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/public/category/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryDTO categoryDTO){
        CategoryDTO response = categoryService.updateCategory(id, categoryDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}