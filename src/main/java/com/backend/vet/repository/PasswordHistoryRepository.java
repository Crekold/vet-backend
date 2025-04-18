package com.backend.vet.repository;

import com.backend.vet.model.PasswordHistory;
import com.backend.vet.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    // Encuentra las últimas N contraseñas para un usuario, ordenadas por fecha de creación descendente
    List<PasswordHistory> findByUsuarioOrderByCreationDateDesc(Usuario usuario, Pageable pageable);

    // Encuentra todas las contraseñas para un usuario, ordenadas por fecha de creación ascendente (para podar)
    List<PasswordHistory> findByUsuarioOrderByCreationDateAsc(Usuario usuario);

    // Cuenta las entradas de historial para un usuario
    long countByUsuario(Usuario usuario);
}
