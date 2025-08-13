package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.model.Category;
import com.ecom.sb_ecom.payload.CategoryDTO;
import com.ecom.sb_ecom.payload.CategoryResponse;

public interface CategoryService {

    CategoryResponse findAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    Category findById(Long id);
    Category findByName(String name);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long id);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
}
