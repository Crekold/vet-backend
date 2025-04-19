package com.backend.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.vet.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}