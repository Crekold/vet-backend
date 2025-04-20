package com.backend.vet.repository;

import com.backend.vet.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByMascotaId(Long mascotaId);
    List<Cita> findByMascotaClienteId(Long clienteId);
    List<Cita> findByUsuarioId(Long usuarioId);
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<Cita> findByEstado(String estado);
    List<Cita> findByFechaGreaterThanEqual(LocalDate fecha);

    /**
     * Cuenta las citas programadas para hoy
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE CAST(c.fecha AS LocalDate) = CURRENT_DATE")
    int countCitasDelDia();

    /**
     * Obtiene las próximas 10 citas programadas a partir de hoy
     */
    @Query("SELECT c FROM Cita c WHERE c.fecha >= CURRENT_TIMESTAMP ORDER BY c.fecha ASC")
    List<Cita> findProximasCitas();
}
