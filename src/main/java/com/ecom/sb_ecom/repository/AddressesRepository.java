package com.ecom.sb_ecom.repository;

import com.ecom.sb_ecom.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressesRepository extends JpaRepository<Address,Long> {
    @Query("select a from Address a where a.user.userId = ?1")
    Page<Address> findByUserId(Long userId, Pageable pageable);
}
