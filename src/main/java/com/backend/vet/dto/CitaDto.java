package com.backend.vet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDto {
    private Long id;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;
    
    private String motivo;
    
    @Size(max = 20, message = "El estado no debe exceder los 20 caracteres")
    private String estado = "Pendiente";
    
    private Long mascotaId;
    private String mascotaNombre;
    
    private Long usuarioId; // ID del veterinario
    private String usuarioNombre; // Nombre del veterinario
    
    private Long clienteId; // ID del propietario de la mascota
    private String clienteNombre; // Nombre del propietario
}
