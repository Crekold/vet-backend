package com.backend.vet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaServicioDto {
    
    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;
    
    @NotNull(message = "El ID del servicio es obligatorio")
    private Long servicioId;
    
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad = 1;
    
    // Campos adicionales para mostrar información
    private String nombreServicio;
    private String descripcionServicio;
    private BigDecimal precioUnitario;
    private BigDecimal precioTotal;
}
