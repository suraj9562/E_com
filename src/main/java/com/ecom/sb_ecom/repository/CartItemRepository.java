package com.ecom.sb_ecom.repository;

import com.ecom.sb_ecom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.productId = ?2")
    Optional<CartItem> findByCartAndProduct(Long cartId,  Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.productId = ?2")
    void deleteCartItemByProductAndCart(Long cartId, Long productId);
}
