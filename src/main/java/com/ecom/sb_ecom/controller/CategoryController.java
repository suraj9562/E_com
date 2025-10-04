package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.config.AppConstants;
import com.ecom.sb_ecom.payload.CategoryDTO;
import com.ecom.sb_ecom.payload.CategoryResponse;
import com.ecom.sb_ecom.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Category related Endpoints")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Fetch all Categories", description = "Fetches all the categories available, sorted and paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetch all categories"),
            @ApiResponse(responseCode = "400", description = "No category present", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        return new ResponseEntity<>(categoryService.findAll(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @Operation(summary = "Create new Category", description = "Create new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created New Category"),
            @ApiResponse(responseCode = "400", description = "Category Already Exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/public/category")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO response = categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete the Category", description = "Delete the Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category with provided id has been deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category Does not exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @DeleteMapping("/public/category/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@Parameter(description = "ID of category which you want to delete") @PathVariable("id") Long id){
        CategoryDTO response = categoryService.deleteCategory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update the Category", description = "Update the Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category with provided id has been updated successfully"),
            @ApiResponse(responseCode = "400", description = "Category already exists with same name", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category Does not exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PutMapping("/public/category/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@Parameter(description = "ID of category which you want to update") @PathVariable("id") Long id, @RequestBody CategoryDTO categoryDTO){
        CategoryDTO response = categoryService.updateCategory(id, categoryDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}