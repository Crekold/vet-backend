package com.backend.vet.controller;

import com.backend.vet.dto.HistorialClinicoDto;
import com.backend.vet.service.HistorialClinicoService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(HistorialClinicoController.class);
    
    @Autowired
    private HistorialClinicoService historialClinicoService;
    
    @Operation(summary = "Obtener todos los registros de historial clínico", description = "${api.historialClinico.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<List<HistorialClinicoDto>> getAllHistorialClinico() {
        logger.info("Consultando todos los registros de historial clínico");
        List<HistorialClinicoDto> historiales = historialClinicoService.getAllHistorialClinico();
        logger.debug("Se encontraron {} registros de historial clínico", historiales.size());
        return ResponseUtil.ok(historiales);
    }
    
    @Operation(summary = "Obtener historial clínico por ID", description = "${api.historialClinico.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<HistorialClinicoDto> getHistorialClinicoById(
            @Parameter(description = "ID del registro de historial clínico", required = true)
            @PathVariable Long id) {
        logger.info("Buscando historial clínico con ID: {}", id);
        HistorialClinicoDto historialClinico = historialClinicoService.getHistorialClinicoById(id);
        if (historialClinico == null) {
            logger.warn("No se encontró el historial clínico con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.debug("Historial clínico encontrado con ID: {}", id);
        return ResponseUtil.ok(historialClinico);
    }
    
    @Operation(summary = "Crear un nuevo registro de historial clínico", description = "${api.historialClinico.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_CREATE')")
    public ResponseEntity<HistorialClinicoDto> createHistorialClinico(
            @Parameter(description = "Datos del registro de historial clínico", required = true)
            @Valid @RequestBody HistorialClinicoDto historialClinicoDto) {
        logger.info("Creando nuevo registro de historial clínico para mascota ID: {}", historialClinicoDto.getMascotaId());
        try {
            HistorialClinicoDto created = historialClinicoService.createHistorialClinico(historialClinicoDto);
            logger.info("Historial clínico creado exitosamente con ID: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error al crear historial clínico: {}", e.getMessage());
            throw e;
        }
    }
    
    @Operation(summary = "Actualizar registro de historial clínico", description = "${api.historialClinico.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_UPDATE')")
    public ResponseEntity<HistorialClinicoDto> updateHistorialClinico(
            @Parameter(description = "ID del registro de historial clínico", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del registro", required = true)
            @Valid @RequestBody HistorialClinicoDto historialClinicoDto) {
        logger.info("Actualizando historial clínico con ID: {}", id);
        HistorialClinicoDto updatedHistorialClinico = historialClinicoService.updateHistorialClinico(id, historialClinicoDto);
        if (updatedHistorialClinico == null) {
            logger.warn("No se pudo actualizar el historial clínico con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Historial clínico actualizado exitosamente con ID: {}", id);
        return ResponseEntity.ok(updatedHistorialClinico);
    }
    
    @Operation(summary = "Eliminar registro de historial clínico", description = "${api.historialClinico.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_DELETE')")
    public ResponseEntity<Void> deleteHistorialClinico(
            @Parameter(description = "ID del registro de historial clínico", required = true)
            @PathVariable Long id) {
        logger.info("Eliminando historial clínico con ID: {}", id);
        boolean deleted = historialClinicoService.deleteHistorialClinico(id);
        if (!deleted) {
            logger.warn("No se pudo eliminar el historial clínico con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Historial clínico eliminado exitosamente con ID: {}", id);
        return ResponseUtil.deleteResponse(deleted);
    }
    
    @Operation(summary = "Obtener historial clínico por mascota", description = "${api.historialClinico.getByMascota.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/mascota/{mascotaId}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByMascotaId(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long mascotaId) {
        logger.info("Consultando historiales clínicos para la mascota ID: {}", mascotaId);
        List<HistorialClinicoDto> historiales = historialClinicoService.getHistorialClinicoByMascotaId(mascotaId);
        logger.debug("Se encontraron {} registros para la mascota ID: {}", historiales.size(), mascotaId);
        return ResponseEntity.ok(historiales);
    }
    
    @Operation(summary = "Obtener historial clínico por cliente", description = "${api.historialClinico.getByCliente.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByClienteId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        logger.info("Consultando historiales clínicos para el cliente ID: {}", clienteId);
        List<HistorialClinicoDto> historiales = historialClinicoService.getHistorialClinicoByClienteId(clienteId);
        logger.debug("Se encontraron {} registros para el cliente ID: {}", historiales.size(), clienteId);
        return ResponseEntity.ok(historiales);
    }
    
    @Operation(summary = "Obtener historial clínico por veterinario", description = "${api.historialClinico.getByVeterinario.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/veterinario/{veterinarioId}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByVeterinarioId(
            @Parameter(description = "ID del veterinario", required = true)
            @PathVariable Long veterinarioId) {
        logger.info("Consultando historiales clínicos para el veterinario ID: {}", veterinarioId);
        List<HistorialClinicoDto> historiales = historialClinicoService.getHistorialClinicoByVeterinarioId(veterinarioId);
        logger.debug("Se encontraron {} registros para el veterinario ID: {}", historiales.size(), veterinarioId);
        return ResponseEntity.ok(historiales);
    }
    
    @Operation(summary = "Obtener historial clínico por cita", description = "${api.historialClinico.getByCita.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/cita/{citaId}")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByCitaId(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        logger.info("Consultando historiales clínicos para la cita ID: {}", citaId);
        List<HistorialClinicoDto> historiales = historialClinicoService.getHistorialClinicoByCitaId(citaId);
        logger.debug("Se encontraron {} registros para la cita ID: {}", historiales.size(), citaId);
        return ResponseEntity.ok(historiales);
    }
    
    @Operation(summary = "Obtener historial clínico por rango de fechas", description = "${api.historialClinico.getByFechaRango.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/fecha")
    @PreAuthorize("hasAuthority('HISTORIAL_CLINICO_READ')")
    public ResponseEntity<List<HistorialClinicoDto>> getHistorialClinicoByFechaRango(
            @Parameter(description = "Fecha y hora de inicio", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fecha y hora de fin", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        logger.info("Consultando historiales clínicos entre {} y {}", inicio, fin);
        List<HistorialClinicoDto> historiales = historialClinicoService.getHistorialClinicoByFechaRango(inicio, fin);
        logger.debug("Se encontraron {} registros en el rango de fechas especificado", historiales.size());
        return ResponseEntity.ok(historiales);
    }
}
