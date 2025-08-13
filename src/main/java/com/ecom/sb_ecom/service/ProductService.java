package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.payload.ProductDTO;
import com.ecom.sb_ecom.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
//    public Product getProductById(Long id);
//    public Product getProductByName(String productName);
//    public Product updateProduct(Long productId, Product product);
//    public Product deleteProductById(Long id);
    public ProductDTO addProduct(ProductDTO productdto, Long categoryId);
    public ProductResponse searchProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    public ProductResponse searchProductByName(String productName, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO deleteProduct(Long productId);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
