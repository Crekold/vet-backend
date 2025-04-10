package com.backend.vet.repository;

import com.backend.vet.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    
    /**
     * Encuentra el ID más alto en la tabla de roles
     * @return ID máximo actual
     */
    @Query("SELECT MAX(r.id) FROM Role r")
    Optional<Long> findMaxId();
}
