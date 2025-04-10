package com.backend.vet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String mensaje;
    private String token;
    private String tipo;
    private String nombreUsuario;
    private Collection<?> roles;
}
