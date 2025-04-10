package com.backend.vet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private Long id;
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no debe exceder los 50 caracteres")
    private String nombreUsuario;
    
    @Email(message = "El correo debe ser válido")
    @Size(max = 100, message = "El correo no debe exceder los 100 caracteres")
    private String correo;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;
    
    private Long rolId;
    private String rolNombre;
    
    // Campo para la especialidad (solo relevante para veterinarios)
    @Size(max = 100, message = "La especialidad no debe exceder los 100 caracteres")
    private String especialidad;
}
