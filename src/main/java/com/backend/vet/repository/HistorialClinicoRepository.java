package com.backend.vet.repository;

import com.backend.vet.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByMascotaId(Long mascotaId);
    List<HistorialClinico> findByMascotaClienteId(Long clienteId);
    List<HistorialClinico> findByUsuarioId(Long usuarioId);
    List<HistorialClinico> findByCitaId(Long citaId);
    List<HistorialClinico> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
