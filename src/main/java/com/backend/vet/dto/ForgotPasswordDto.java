package com.backend.vet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForgotPasswordDto {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String email;
}
