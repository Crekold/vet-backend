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
    
    @Operation(summary = "Obtener todas las citas", description = "${api.cita.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getAllCitas() {
        return ResponseUtil.ok(citaService.getAllCitas());
    }
    
    @Operation(summary = "Obtener cita por ID", description = "${api.cita.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<CitaDto> getCitaById(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id) {
        CitaDto cita = citaService.getCitaById(id);
        return cita != null ? ResponseUtil.ok(cita) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Crear una nueva cita", description = "${api.cita.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CITA_CREATE')")
    public ResponseEntity<CitaDto> createCita(
            @Parameter(description = "Datos de la cita", required = true)
            @Valid @RequestBody CitaDto citaDto) {
        return ResponseUtil.created(citaService.createCita(citaDto));
    }
    
    @Operation(summary = "Actualizar cita", description = "${api.cita.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CITA_UPDATE')")
    public ResponseEntity<CitaDto> updateCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados de la cita", required = true)
            @Valid @RequestBody CitaDto citaDto) {
        CitaDto updatedCita = citaService.updateCita(id, citaDto);
        return updatedCita != null ? ResponseUtil.ok(updatedCita) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Eliminar cita", description = "${api.cita.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CITA_DELETE')")
    public ResponseEntity<Void> deleteCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(citaService.deleteCita(id));
    }
    
    @Operation(summary = "Obtener citas por mascota", description = "${api.cita.getByMascota.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/mascota/{mascotaId}")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getCitasByMascotaId(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long mascotaId) {
        return ResponseEntity.ok(citaService.getCitasByMascotaId(mascotaId));
    }
    
    @Operation(summary = "Obtener citas por cliente", description = "${api.cita.getByCliente.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getCitasByClienteId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(citaService.getCitasByClienteId(clienteId));
    }
    
    @Operation(summary = "Obtener citas por veterinario", description = "${api.cita.getByVeterinario.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/veterinario/{veterinarioId}")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getCitasByVeterinarioId(
            @Parameter(description = "ID del veterinario", required = true)
            @PathVariable Long veterinarioId) {
        return ResponseEntity.ok(citaService.getCitasByVeterinarioId(veterinarioId));
    }
    
    @Operation(summary = "Obtener citas por rango de fechas", description = "${api.cita.getByFechaRango.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/fecha")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getCitasByFechaRango(
            @Parameter(description = "Fecha de inicio", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha de fin", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(citaService.getCitasByFechaRango(inicio, fin));
    }
    
    @Operation(summary = "Obtener citas por estado", description = "${api.cita.getByEstado.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getCitasByEstado(
            @Parameter(description = "Estado de la cita (Pendiente, Atendida, Cancelada)", required = true)
            @PathVariable String estado) {
        return ResponseEntity.ok(citaService.getCitasByEstado(estado));
    }

    @Operation(summary = "Obtener próximas citas", 
          description = "Proporciona la lista de próximas citas programadas a partir de hoy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas obtenida correctamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado para acceder a este recurso")
    })
    @GetMapping("/proximas")
    @PreAuthorize("hasAuthority('CITA_READ')")
    public ResponseEntity<List<CitaDto>> getProximasCitas() {
        return ResponseUtil.ok(citaService.findProximasCitas());
    }
}
