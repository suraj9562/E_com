package com.ecom.sb_ecom.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("productName")
    @Size(min = 3, message = "Product name size must be greater than 3")
    @Size(max = 100, message = "Product name size must be less than 100")
    private String productName;

    @JsonProperty("image")
    private String image;

    @JsonProperty("description")
    @Size(min = 10, message = "Product description size must be greater than 10")
    @Size(max = 500, message = "Product description size must be less than 500")
    private String description;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("discount")
    private Double discount;

    @JsonProperty("specialPrice")
    private Double specialPrice;
}
