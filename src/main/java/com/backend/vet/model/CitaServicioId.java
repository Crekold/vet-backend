package com.backend.vet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaServicioId implements Serializable {
    
    @Column(name = "id_cita")
    private Long citaId;
    
    @Column(name = "id_servicio")
    private Long servicioId;
}
