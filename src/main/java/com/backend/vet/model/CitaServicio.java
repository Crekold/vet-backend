package com.backend.vet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cita_servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaServicio {
    
    @EmbeddedId
    private CitaServicioId id;
    
    @ManyToOne
    @MapsId("citaId")
    @JoinColumn(name = "id_cita")
    private Cita cita;
    
    @ManyToOne
    @MapsId("servicioId")
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;
    
    @Column(nullable = false)
    private Integer cantidad = 1;
}
