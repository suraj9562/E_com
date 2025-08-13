package com.ecom.sb_ecom.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @JsonProperty("categoryName")
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 5, message = "Category name must contain at least 5 characters")
    @Size(max = 100, message = "Category name must be less than 100 characters")
    private String categoryName;

    @JsonProperty("categoryId")
    private Long categoryId;
}
