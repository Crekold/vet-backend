package com.backend.vet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String nombre;
    
    @Size(max = 100, message = "El apellido no debe exceder los 100 caracteres")
    private String apellido;
    
    @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres")
    private String telefono;
    
    @Email(message = "El correo debe ser válido")
    @Size(max = 100, message = "El correo no debe exceder los 100 caracteres")
    private String correo;
    
    private String direccion;
    private LocalDateTime fechaRegistro;
}
