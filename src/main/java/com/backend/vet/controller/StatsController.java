package com.backend.vet.controller;

import com.backend.vet.service.HistorialClinicoService;
import com.backend.vet.service.CitaService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Estadísticas", description = "API para obtener estadísticas del dashboard veterinario")
public class StatsController {

    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);

    @Autowired
    private HistorialClinicoService historialClinicoService;
    
    @Autowired
    private CitaService citaService;
    
    @Operation(summary = "Obtener estadísticas del dashboard", 
              description = "Proporciona estadísticas como pacientes atendidos, citas del día y vacunas aplicadas")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('STATS_READ')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        logger.info("Obteniendo estadísticas del dashboard");
        Map<String, Object> stats = new HashMap<>();
        
        long pacientesAtendidos = historialClinicoService.countPacientesAtendidos();
        long citasDelDia = citaService.countCitasDelDia();
        long vacunasAplicadas = historialClinicoService.countVacunasAplicadasHoy();
        
        stats.put("pacientesAtendidos", pacientesAtendidos);
        stats.put("citasDelDia", citasDelDia);
        stats.put("vacunasAplicadas", vacunasAplicadas);
        
        logger.debug("Estadísticas obtenidas - Pacientes: {}, Citas: {}, Vacunas: {}", 
                    pacientesAtendidos, citasDelDia, vacunasAplicadas);
        
        return ResponseUtil.ok(stats);
    }
}
