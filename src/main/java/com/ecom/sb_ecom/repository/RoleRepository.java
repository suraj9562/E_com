package com.ecom.sb_ecom.repository;

import com.ecom.sb_ecom.model.AppRole;
import com.ecom.sb_ecom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
