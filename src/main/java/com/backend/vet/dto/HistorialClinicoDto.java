package com.backend.vet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialClinicoDto {
    private Long id;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;
    
    private String diagnostico;
    
    private String tratamiento;
    
    private String observaciones;
    
    private Long mascotaId;
    private String mascotaNombre;
    
    private Long usuarioId; // ID del veterinario
    private String usuarioNombre; // Nombre del veterinario
    
    private Long citaId; // ID de la cita asociada
    
    private Long clienteId; // ID del propietario de la mascota
    private String clienteNombre; // Nombre del propietario
}
