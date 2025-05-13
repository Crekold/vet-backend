package com.backend.vet.controller;

import com.backend.vet.dto.CitaServicioDto;
import com.backend.vet.service.CitaServicioService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cita-servicios")
@Tag(name = "Servicios por Cita", description = "API para gestionar los servicios médicos asociados a las citas")
public class CitaServicioController {
    
    private static final Logger logger = LoggerFactory.getLogger(CitaServicioController.class);
    
    @Autowired
    private CitaServicioService citaServicioService;
    
    @Operation(summary = "Obtener servicios por cita", description = "${api.citaServicio.getByCita.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/cita/{citaId}")
    @PreAuthorize("hasAuthority('CITA_SERVICIO_READ')")
    public ResponseEntity<List<CitaServicioDto>> getServiciosByCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        logger.info("Consultando servicios para la cita ID: {}", citaId);
        List<CitaServicioDto> servicios = citaServicioService.getServiciosByCita(citaId);
        logger.debug("Se encontraron {} servicios para la cita ID: {}", servicios.size(), citaId);
        return ResponseUtil.ok(servicios);
    }
    
    @Operation(summary = "Obtener citas por servicio", description = "${api.citaServicio.getByServicio.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/servicio/{servicioId}")
    @PreAuthorize("hasAuthority('CITA_SERVICIO_READ')")
    public ResponseEntity<List<CitaServicioDto>> getCitasByServicio(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long servicioId) {
        logger.info("Consultando citas para el servicio ID: {}", servicioId);
        List<CitaServicioDto> citas = citaServicioService.getCitasByServicio(servicioId);
        logger.debug("Se encontraron {} citas para el servicio ID: {}", citas.size(), servicioId);
        return ResponseEntity.ok(citas);
    }
    
    @Operation(summary = "Agregar servicio a cita", description = "${api.citaServicio.addServicio.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CITA_SERVICIO_CREATE')")
    public ResponseEntity<CitaServicioDto> addServicioToCita(
            @Parameter(description = "Datos del servicio a agregar", required = true)
            @Valid @RequestBody CitaServicioDto citaServicioDto) {
        logger.info("Agregando servicio ID: {} a la cita ID: {}", 
                   citaServicioDto.getServicioId(), citaServicioDto.getCitaId());
        try {
            CitaServicioDto resultado = citaServicioService.addServicioToCita(citaServicioDto);
            logger.info("Servicio agregado exitosamente a la cita");
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            logger.error("Error al agregar servicio a la cita: {}", e.getMessage());
            throw e;
        }
    }
    
    @Operation(summary = "Actualizar servicio en cita", description = "${api.citaServicio.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping
    @PreAuthorize("hasAuthority('CITA_SERVICIO_UPDATE')")
    public ResponseEntity<CitaServicioDto> updateCitaServicio(
            @Parameter(description = "Datos actualizados del servicio", required = true)
            @Valid @RequestBody CitaServicioDto citaServicioDto) {
        logger.info("Actualizando servicio ID: {} para la cita ID: {}", 
                   citaServicioDto.getServicioId(), citaServicioDto.getCitaId());
        try {
            CitaServicioDto resultado = citaServicioService.updateCitaServicio(citaServicioDto);
            logger.info("Servicio actualizado exitosamente en la cita");
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            logger.error("Error al actualizar servicio en la cita: {}", e.getMessage());
            throw e;
        }
    }
    
    @Operation(summary = "Eliminar servicio de cita", description = "${api.citaServicio.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/cita/{citaId}/servicio/{servicioId}")
    @PreAuthorize("hasAuthority('CITA_SERVICIO_DELETE')")
    public ResponseEntity<Void> removeServicioFromCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId,
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long servicioId) {
        logger.info("Eliminando servicio ID: {} de la cita ID: {}", servicioId, citaId);
        boolean removed = citaServicioService.removeServicioFromCita(citaId, servicioId);
        if (!removed) {
            logger.warn("No se pudo eliminar el servicio ID: {} de la cita ID: {}", servicioId, citaId);
            return ResponseUtil.notFound();
        }
        logger.info("Servicio eliminado exitosamente de la cita");
        return ResponseUtil.deleteResponse(true);
    }
    
    @Operation(summary = "Eliminar todos los servicios de una cita", description = "${api.citaServicio.deleteAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/cita/{citaId}")
    @PreAuthorize("hasAuthority('CITA_SERVICIO_DELETE')")
    public ResponseEntity<Void> removeAllServiciosFromCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        logger.info("Eliminando todos los servicios de la cita ID: {}", citaId);
        boolean removed = citaServicioService.removeAllServiciosFromCita(citaId);
        if (!removed) {
            logger.warn("No se pudieron eliminar los servicios de la cita ID: {}", citaId);
            return ResponseUtil.notFound();
        }
        logger.info("Todos los servicios eliminados exitosamente de la cita ID: {}", citaId);
        return ResponseUtil.deleteResponse(removed);
    }
}
