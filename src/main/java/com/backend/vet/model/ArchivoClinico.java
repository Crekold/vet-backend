package com.backend.vet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "archivos_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoClinico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;
    
    @Column(nullable = false)
    private String url;
    
    @Column(name = "tipo_mime", length = 50)
    private String tipoMime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historial")
    private HistorialClinico historialClinico;
}
