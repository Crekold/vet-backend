package com.backend.vet.controller;

import com.backend.vet.service.HistorialClinicoService;
import com.backend.vet.service.CitaService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    @Autowired
    private HistorialClinicoService historialClinicoService;
    
    @Autowired
    private CitaService citaService;
    
    @Operation(summary = "Obtener estadísticas del dashboard", 
              description = "Proporciona estadísticas como pacientes atendidos, citas del día y vacunas aplicadas")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'EMPLEADO')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pacientesAtendidos", historialClinicoService.countPacientesAtendidos());
        stats.put("citasDelDia", citaService.countCitasDelDia());
        stats.put("vacunasAplicadas", historialClinicoService.countVacunasAplicadasHoy());
        
        return ResponseUtil.ok(stats);
    }
}
