package com.ecom.sb_ecom.repository;

import com.ecom.sb_ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c From Cart c WHERE c.user.userId = ?1")
    Optional<Cart> findByUserId(Long userId);

    @Query("SELECT c from Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.productId = ?1")
    List<Cart> findCartsByProductId(Long ProductId);

    @Query("SELECT c from Cart c where c.user.email = ?1")
    Optional<Cart> findCartByEmailId(String EmailId);
}
