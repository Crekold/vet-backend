package com.backend.vet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDto {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no debe exceder los 50 caracteres")
    private String nombreUsuario;

    @Email(message = "El correo debe ser válido")
    @Size(max = 100, message = "El correo no debe exceder los 100 caracteres")
    private String correo;

    // La contraseña es opcional en la actualización.
    // Si se proporciona, debe cumplir con la complejidad y longitud.
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)")
    private String contrasena; // Sin @NotBlank

    private Long rolId; // Permitir actualizar el rol

    @Size(max = 100, message = "La especialidad no debe exceder los 100 caracteres")
    private String especialidad;

    // No incluimos 'activo' aquí, ya que se maneja por 'deleteUsuario' (soft delete)
}
