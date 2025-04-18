package com.backend.vet.dto;

import com.fasterxml.jackson.annotation.JsonInclude; // Importar JsonInclude
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluir nulos en JSON
public class LoginResponseDto {
    private String mensaje;
    private String token;
    private String tipo;
    private String nombreUsuario;
    private Collection<?> roles;
    private Boolean passwordChangeRequired; // Nuevo campo (Boolean para permitir null)
}
