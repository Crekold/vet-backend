package com.backend.vet.controller;

import com.backend.vet.dto.HistorialClinicoDto;
import com.backend.vet.service.HistorialClinicoService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/historial-clinico")
@Tag(name = "Historial Clínico", description = "API para la gestión del historial clínico de mascotas")
public class HistorialClinicoController {
    
    @Autowired
    private HistorialClinicoService historialClinicoService;
    
    @Operation(summary = "Obtener todos los registros de historial clínico", description = "${api.historialClinico.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<List<HistorialClinicoDto>> getAllHistorialClinico() {
        return ResponseUtil.ok(historialClinicoService.getAllHistorialClinico());
    }
    
    @Operation(summary = "Obtener historial clínico por ID", description = "${api.historialClinico.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialClinicoDto> getHistorialClinicoById(
            @Parameter(description = "ID del registro de historial clínico", required = true)
            @PathVariable Long id) {
        HistorialClinicoDto historialClinico = historialClinicoService.getHistorialClinicoById(id);
        return historialClinico != null ? ResponseUtil.ok(historialClinico) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Crear un nuevo registro de historial clínico", description = "${api.historialClinico.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasRole('VETERINARIO') or hasRole('ADMIN')")
    public ResponseEntity<HistorialClinicoDto> createHistorialClinico(
            @Parameter(description = "Datos del registro de historial clínico", required = true)
            @Valid @RequestBody HistorialClinicoDto historialClinicoDto) {
        return ResponseEntity.ok(historialClinicoService.createHistorialClinico(historialClinicoDto));
    }
    
    @Operation(summary = "Actualizar registro de historial clínico", description = "${api.historialClinico.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARIO') or hasRole('ADMIN')")
    public ResponseEntity<HistorialClinicoDto> updateHistorialClinico(
            @Parameter(description = "ID del registro de historial clínico", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del registro", required = true)
            @Valid @RequestBody HistorialClinicoDto historialClinicoDto) {
        HistorialClinicoDto updatedHistorialClinico = historialClinicoService.updateHistorialClinico(id, historialClinicoDto);
        if (updatedHistorialClinico != null) {
            return ResponseEntity.ok(updatedHistorialClinico);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Eliminar registro de historial clínico", description = "${api.historialClinico.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHistorialClinico(
            @Parameter(description = "ID del registro de historial clínico", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(historialClinicoService.deleteHistorialClinico(id));
    }
    
    @Operation(summary = "Obtener historial clínico por mascota", description = "Retorna todos los registros de una mascota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByMascotaId(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long mascotaId) {
        return ResponseEntity.ok(historialClinicoService.getHistorialClinicoByMascotaId(mascotaId));
    }
    
    @Operation(summary = "Obtener historial clínico por cliente", description = "Retorna todos los registros de las mascotas de un cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByClienteId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(historialClinicoService.getHistorialClinicoByClienteId(clienteId));
    }
    
    @Operation(summary = "Obtener historial clínico por veterinario", description = "Retorna todos los registros creados por un veterinario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByVeterinarioId(
            @Parameter(description = "ID del veterinario", required = true)
            @PathVariable Long veterinarioId) {
        return ResponseEntity.ok(historialClinicoService.getHistorialClinicoByVeterinarioId(veterinarioId));
    }
    
    @Operation(summary = "Obtener historial clínico por cita", description = "Retorna el registro de historial clínico asociado a una cita")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByCitaId(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        return ResponseEntity.ok(historialClinicoService.getHistorialClinicoByCitaId(citaId));
    }
    
    @Operation(summary = "Obtener historial clínico por rango de fechas", description = "Retorna todos los registros dentro de un rango de fechas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/fecha")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByFechaRango(
            @Parameter(description = "Fecha y hora de inicio", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fecha y hora de fin", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(historialClinicoService.getHistorialClinicoByFechaRango(inicio, fin));
    }
}
