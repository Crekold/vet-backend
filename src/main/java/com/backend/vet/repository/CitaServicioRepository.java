package com.backend.vet.repository;

import com.backend.vet.model.CitaServicio;
import com.backend.vet.model.CitaServicioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaServicioRepository extends JpaRepository<CitaServicio, CitaServicioId> {
    List<CitaServicio> findByCitaId(Long citaId);
    List<CitaServicio> findByServicioId(Long servicioId);
    void deleteByCitaId(Long citaId);
}
