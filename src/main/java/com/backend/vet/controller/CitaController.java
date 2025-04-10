package com.backend.vet.controller;

import com.backend.vet.dto.CitaDto;
import com.backend.vet.service.CitaService;
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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "API para la gestión de citas veterinarias")
public class CitaController {
    
    @Autowired
    private CitaService citaService;
    
    @Operation(summary = "Obtener todas las citas", description = "Retorna todas las citas registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<List<CitaDto>> getAllCitas() {
        return ResponseUtil.ok(citaService.getAllCitas());
    }
    
    @Operation(summary = "Obtener cita por ID", description = "Retorna una cita por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CitaDto> getCitaById(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id) {
        CitaDto cita = citaService.getCitaById(id);
        return cita != null ? ResponseUtil.ok(cita) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Crear una nueva cita", description = "Crea una nueva cita veterinaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cita creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<CitaDto> createCita(
            @Parameter(description = "Datos de la cita", required = true)
            @Valid @RequestBody CitaDto citaDto) {
        return ResponseUtil.created(citaService.createCita(citaDto));
    }
    
    @Operation(summary = "Actualizar cita", description = "Actualiza los datos de una cita existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CitaDto> updateCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados de la cita", required = true)
            @Valid @RequestBody CitaDto citaDto) {
        CitaDto updatedCita = citaService.updateCita(id, citaDto);
        return updatedCita != null ? ResponseUtil.ok(updatedCita) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Eliminar cita", description = "Elimina una cita existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cita eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<Void> deleteCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(citaService.deleteCita(id));
    }
    
    @Operation(summary = "Obtener citas por mascota", description = "Retorna todas las citas de una mascota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<CitaDto>> getCitasByMascotaId(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long mascotaId) {
        return ResponseEntity.ok(citaService.getCitasByMascotaId(mascotaId));
    }
    
    @Operation(summary = "Obtener citas por cliente", description = "Retorna todas las citas de las mascotas de un cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CitaDto>> getCitasByClienteId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(citaService.getCitasByClienteId(clienteId));
    }
    
    @Operation(summary = "Obtener citas por veterinario", description = "Retorna todas las citas asignadas a un veterinario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<CitaDto>> getCitasByVeterinarioId(
            @Parameter(description = "ID del veterinario", required = true)
            @PathVariable Long veterinarioId) {
        return ResponseEntity.ok(citaService.getCitasByVeterinarioId(veterinarioId));
    }
    
    @Operation(summary = "Obtener citas por rango de fechas", description = "Retorna todas las citas dentro de un rango de fechas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/fecha")
    public ResponseEntity<List<CitaDto>> getCitasByFechaRango(
            @Parameter(description = "Fecha de inicio", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha de fin", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(citaService.getCitasByFechaRango(inicio, fin));
    }
    
    @Operation(summary = "Obtener citas por estado", description = "Retorna todas las citas con un estado específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CitaDto>> getCitasByEstado(
            @Parameter(description = "Estado de la cita (Pendiente, Atendida, Cancelada)", required = true)
            @PathVariable String estado) {
        return ResponseEntity.ok(citaService.getCitasByEstado(estado));
    }
}
