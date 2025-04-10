package com.backend.vet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDto {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String nombre;
    
    @Size(max = 50, message = "La especie no debe exceder los 50 caracteres")
    private String especie;
    
    @Size(max = 50, message = "La raza no debe exceder los 50 caracteres")
    private String raza;
    
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;
    
    @Pattern(regexp = "^(Macho|Hembra)$", message = "El sexo debe ser 'Macho' o 'Hembra'")
    private String sexo;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    // Campos adicionales para información del cliente
    private String clienteNombre;
    private String clienteApellido;
}
