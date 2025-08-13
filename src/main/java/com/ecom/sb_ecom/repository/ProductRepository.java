package com.ecom.sb_ecom.repository;

import com.ecom.sb_ecom.model.Category;
import com.ecom.sb_ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Page<Product> findByCategory(Category category, Pageable pageable);
    public Page<Product> findByProductNameLikeIgnoreCase(String productName, Pageable pageable);
}
