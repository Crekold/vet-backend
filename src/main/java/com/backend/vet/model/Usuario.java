package com.backend.vet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
