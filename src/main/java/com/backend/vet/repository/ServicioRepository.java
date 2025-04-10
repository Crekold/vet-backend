package com.backend.vet.repository;

import com.backend.vet.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);
    List<Servicio> findByPrecioLessThanEqual(BigDecimal precio);
    List<Servicio> findByPrecioGreaterThanEqual(BigDecimal precio);
}
