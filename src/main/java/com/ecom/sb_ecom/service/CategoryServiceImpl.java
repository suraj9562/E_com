package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecom.sb_ecom.model.Category;
import com.ecom.sb_ecom.payload.CategoryDTO;
import com.ecom.sb_ecom.payload.CategoryResponse;
import com.ecom.sb_ecom.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse findAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();

        if(categories.isEmpty())
            throw new ApiException("No category found, Please add at least one category");

        List<CategoryDTO> categoryDTOS = categories.stream().map(
             category -> modelMapper.map(category, CategoryDTO.class)
        ).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public Category findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);

        if(category.isEmpty()){
            throw new ResourceNotFoundException("Category", "id", id);
        }

        return category.get();
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository
                .findByCategoryName(name)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "name", name)
                );
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        Optional<Category> existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(existingCategory.isPresent()){
            throw new ApiException("Category with name " + category.getCategoryName() + " already exists");
        }

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category categoryToBeDeleted = categoryRepository
                .findById(id)
                .orElseThrow(
                        ()->new ResourceNotFoundException("Category", "id", id)
                );

        categoryRepository.deleteById(id);
        return modelMapper.map(categoryToBeDeleted, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        Category t = categoryRepository
                .findById(id)
                .orElseThrow(
                        ()->new ResourceNotFoundException("Category", "id", id)
                );

        Optional<Category> existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(existingCategory.isPresent()){
            throw new ApiException("Category with name " + category.getCategoryName() + " already exists");
        }

        category.setCategoryId(t.getCategoryId());
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }
}
