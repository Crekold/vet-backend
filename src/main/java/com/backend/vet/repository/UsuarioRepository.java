package com.backend.vet.repository;

import com.backend.vet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByCorreo(String correo);
    Boolean existsByNombreUsuario(String nombreUsuario);
    Boolean existsByCorreo(String correo);
    Optional<Usuario> findByResetToken(String resetToken); // Nuevo método
    List<Usuario> findAllByRolNombreIgnoreCase(String rolNombre);
}
