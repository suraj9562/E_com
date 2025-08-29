package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.config.AppConstants;
import com.ecom.sb_ecom.payload.CartDTO;
import com.ecom.sb_ecom.payload.CartResponse;
import com.ecom.sb_ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCart(){
        return new ResponseEntity<>(cartService.getCart(), HttpStatus.OK);
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable(name = "productId") Long productId,
            @PathVariable(name = "quantity") Integer quantity
    ){
        return new ResponseEntity<>(cartService.addProductToCart(productId, quantity), HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<CartResponse> getAllCarts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CARTS_BY, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        return new ResponseEntity<>(cartService.getAllCarts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }


    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(
            @PathVariable(name = "productId") Long productId,
            @PathVariable(name = "operation") String operation
    ){
        return new ResponseEntity<>(cartService.updateProductQuantity(productId, operation.equalsIgnoreCase("delete") ? -1 : 1), HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable(name = "cartId") Long cartId,
            @PathVariable(name = "productId") Long productId
    ){
        return new ResponseEntity<>(cartService.deleteProductFromCart(cartId, productId), HttpStatus.OK);
    }
}
