package com.backend.vet.repository;

import com.backend.vet.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.time.LocalDate;

import java.util.List;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByMascotaId(Long mascotaId);
    List<HistorialClinico> findByMascotaClienteId(Long clienteId);
    List<HistorialClinico> findByUsuarioId(Long usuarioId);
    List<HistorialClinico> findByCitaId(Long citaId);
    List<HistorialClinico> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Cuenta el número de mascotas distintas atendidas hoy
     */
    @Query("SELECT COUNT(DISTINCT h.mascota.id) FROM HistorialClinico h WHERE CAST(h.fecha AS LocalDate) = CURRENT_DATE")
    int countPacientesAtendidos();

    /**
     * Cuenta el número de vacunas aplicadas hoy
     * Nota: Ajusta esta consulta según tu modelo de datos para vacunas
     */
    @Query("SELECT COUNT(h) FROM HistorialClinico h WHERE CAST(h.fecha AS LocalDate) = CURRENT_DATE AND h.diagnostico LIKE '%vacuna%'")
    int countVacunasAplicadasHoy();
}
