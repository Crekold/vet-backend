package com.backend.vet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // Importar LocalDateTime
import java.util.ArrayList; // Importar ArrayList
import java.util.List; // Importar List

@Data
@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 50)
    private String nombreUsuario;
    
    @Column(name = "correo", unique = true, nullable = false, length = 100)
    private String correo;
    
    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Role rol;
    
    // Campo específico para veterinarios
    @Column(name = "especialidad", length = 100)
    private String especialidad;

    // Nuevos campos para gestión de contraseñas y bloqueo
    @Column(name = "failed_login_attempts", columnDefinition = "INT DEFAULT 0")
    private int failedLoginAttempts = 0;

    @Column(name = "lock_expiration_time")
    private LocalDateTime lockExpirationTime;

    @Column(name = "password_last_changed")
    private LocalDateTime passwordLastChanged;

    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // Nueva relación con el historial de contraseñas
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PasswordHistory> passwordHistory = new ArrayList<>();
}
