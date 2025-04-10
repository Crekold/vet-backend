package com.backend.vet.repository;

import com.backend.vet.model.ArchivoClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoClinicoRepository extends JpaRepository<ArchivoClinico, Long> {
    List<ArchivoClinico> findByHistorialClinicoId(Long historialClinicoId);
    List<ArchivoClinico> findByHistorialClinicoMascotaId(Long mascotaId);
    List<ArchivoClinico> findByNombreArchivoContaining(String nombreArchivo);
    List<ArchivoClinico> findByTipoMime(String tipoMime);
}
