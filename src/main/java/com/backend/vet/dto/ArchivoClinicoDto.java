package com.backend.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoClinicoDto {
    private Long id;
    
    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no debe exceder los 255 caracteres")
    private String nombreArchivo;
    
    @NotBlank(message = "La URL del archivo es obligatoria")
    private String url;
    
    @Size(max = 50, message = "El tipo MIME no debe exceder los 50 caracteres")
    private String tipoMime;
    
    @NotNull(message = "El ID del historial clínico es obligatorio")
    private Long historialClinicoId;
    
    // Información adicional para la respuesta
    private String mascotaNombre;
    private String diagnosticoResumen;
}
