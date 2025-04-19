package com.backend.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.vet.model.Permission;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
}