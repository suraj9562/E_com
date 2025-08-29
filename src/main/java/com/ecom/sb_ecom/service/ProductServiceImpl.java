package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.config.AppConstants;
import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecom.sb_ecom.model.Cart;
import com.ecom.sb_ecom.model.Category;
import com.ecom.sb_ecom.model.Product;
import com.ecom.sb_ecom.payload.CartDTO;
import com.ecom.sb_ecom.payload.ProductDTO;
import com.ecom.sb_ecom.payload.ProductResponse;
import com.ecom.sb_ecom.repository.CartRepository;
import com.ecom.sb_ecom.repository.CategoryRepository;
import com.ecom.sb_ecom.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    ProductRepository productRepository;
    ModelMapper modelMapper;
    CategoryRepository categoryRepository;
    FileService fileService;
    CartRepository cartRepository;
    CartService cartService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ModelMapper modelMapper,
            CategoryRepository categoryRepository,
            FileService fileService,
            CartRepository cartRepository,
            CartService cartService) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO,  Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
        );

        boolean isProductPresent  = false;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductPresent = true;
                break;
            }
        }

        if (isProductPresent) {
            throw new ApiException("Same product with name : "  + productDTO.getProductName() + " already exists");
        }

        Product product = modelMapper.map(productDTO,Product.class);
        product.setCategory(category);
        Double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);
        product.setSpecialPrice(specialPrice);
        product.setImage("default.png");

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findAll(pageable);
        List<Product>  productList = productsPage.getContent();

        if(productList.isEmpty()){
            throw new ApiException("No products found, Please add at least one product");
        }

        List<ProductDTO> productDTOS = productList.stream().map(
                product -> modelMapper.map(product, ProductDTO.class)
        ).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
        );

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByCategory(category, pageable);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new ResourceNotFoundException("Product", "categoryId", categoryId);
        }

        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product, ProductDTO.class)
        ).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return  productResponse;
    }

    @Override
    public ProductResponse searchProductByName(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageable);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new ResourceNotFoundException("Product", "keyword", keyword);
        }

        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product, ProductDTO.class)
        ).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return  productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );

        Product product = modelMapper.map(productDTO, Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOS = carts.stream().map(
                cart -> {
                    CartDTO dto = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> cartItems = cart.getCartItems().stream().map(
                            item -> modelMapper.map(item, ProductDTO.class)
                    ).toList();

                    dto.setCartItems(cartItems);

                    return dto;
                }
        ).toList();

        cartDTOS.forEach(cartDTO -> {
            cartService.updateProductInCarts(cartDTO.getCartId(), productId);
        });

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach( cart -> {
            cartService.deleteProductFromCart(cart.getCartId(), productId);
        });

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );

        // upload image to server
        // get the fileName of uploaded image
        String fileName = fileService.uploadImage(path, image);

        // updating the new fileName to the product
        productFromDb.setImage(fileName);
        Product savedProduct = productRepository.save(productFromDb);

        return  modelMapper.map(savedProduct, ProductDTO.class);
    }
}
