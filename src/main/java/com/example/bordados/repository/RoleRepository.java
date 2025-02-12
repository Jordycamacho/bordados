package com.example.bordados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Role;
import com.example.bordados.model.Enums.RoleEnum;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByRoleEnum(RoleEnum roleEnum);


}
