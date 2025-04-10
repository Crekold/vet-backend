package com.backend.vet.controller;

import com.backend.vet.dto.CitaServicioDto;
import com.backend.vet.service.CitaServicioService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    
    @Autowired
    private CitaServicioService citaServicioService;
    
    @Operation(summary = "Obtener servicios por cita", description = "${api.citaServicio.getByCita.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<CitaServicioDto>> getServiciosByCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        return ResponseUtil.ok(citaServicioService.getServiciosByCita(citaId));
    }
    
    @Operation(summary = "Obtener citas por servicio", description = "Retorna todas las citas asociadas a un servicio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/servicio/{servicioId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<List<CitaServicioDto>> getCitasByServicio(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long servicioId) {
        return ResponseEntity.ok(citaServicioService.getCitasByServicio(servicioId));
    }
    
    @Operation(summary = "Agregar servicio a cita", description = "${api.citaServicio.addServicio.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<CitaServicioDto> addServicioToCita(
            @Parameter(description = "Datos del servicio a agregar", required = true)
            @Valid @RequestBody CitaServicioDto citaServicioDto) {
        return ResponseEntity.ok(citaServicioService.addServicioToCita(citaServicioDto));
    }
    
    @Operation(summary = "Actualizar servicio en cita", description = "Actualiza la cantidad de un servicio en una cita")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Relación cita-servicio no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<CitaServicioDto> updateCitaServicio(
            @Parameter(description = "Datos actualizados del servicio", required = true)
            @Valid @RequestBody CitaServicioDto citaServicioDto) {
        return ResponseEntity.ok(citaServicioService.updateCitaServicio(citaServicioDto));
    }
    
    @Operation(summary = "Eliminar servicio de cita", description = "Elimina un servicio médico de una cita")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Servicio eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Relación cita-servicio no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/cita/{citaId}/servicio/{servicioId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<Void> removeServicioFromCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId,
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long servicioId) {
        boolean removed = citaServicioService.removeServicioFromCita(citaId, servicioId);
        return ResponseUtil.deleteResponse(removed);
    }
    
    @Operation(summary = "Eliminar todos los servicios de una cita", description = "Elimina todos los servicios médicos asociados a una cita")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Servicios eliminados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/cita/{citaId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<Void> removeAllServiciosFromCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        boolean removed = citaServicioService.removeAllServiciosFromCita(citaId);
        return ResponseUtil.deleteResponse(removed);
    }
}
