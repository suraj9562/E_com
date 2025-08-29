package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.payload.CartDTO;
import com.ecom.sb_ecom.payload.CartResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
    CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CartDTO getCart();

    @Transactional
    CartDTO updateProductQuantity(Long productId, Integer quantity);

    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    @Transactional
    void updateProductInCarts(Long cartId, Long productId);
}
