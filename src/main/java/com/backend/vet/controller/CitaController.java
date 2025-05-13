package com.backend.vet.controller;

import com.backend.vet.dto.CitaDto;
import com.backend.vet.service.CitaService;
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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "API para la gestión de citas veterinarias")
public class CitaController {
    
    private static final Logger logger = LoggerFactory.getLogger(CitaController.class);
    
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
        logger.info("Obteniendo todas las citas");
        List<CitaDto> citas = citaService.getAllCitas();
        logger.debug("Se encontraron {} citas", citas.size());
        return ResponseUtil.ok(citas);
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
        logger.info("Buscando cita con ID: {}", id);
        CitaDto cita = citaService.getCitaById(id);
        if (cita == null) {
            logger.warn("No se encontró la cita con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.debug("Cita encontrada con ID: {}", id);
        return ResponseUtil.ok(cita);
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
        logger.info("Creando nueva cita para mascota ID: {} con veterinario ID: {}", 
                   citaDto.getMascotaId(), citaDto.getUsuarioId());
        CitaDto created = citaService.createCita(citaDto);
        logger.info("Cita creada exitosamente con ID: {}", created.getId());
        return ResponseUtil.created(created);
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
        logger.info("Actualizando cita con ID: {}", id);
        CitaDto updatedCita = citaService.updateCita(id, citaDto);
        if (updatedCita == null) {
            logger.warn("No se pudo actualizar la cita con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Cita actualizada exitosamente con ID: {}", id);
        return ResponseUtil.ok(updatedCita);
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
        logger.info("Eliminando cita con ID: {}", id);
        boolean deleted = citaService.deleteCita(id);
        if (!deleted) {
            logger.warn("No se pudo eliminar la cita con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Cita eliminada exitosamente con ID: {}", id);
        return ResponseUtil.deleteResponse(true);
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
        logger.info("Buscando citas para la mascota con ID: {}", mascotaId);
        List<CitaDto> citas = citaService.getCitasByMascotaId(mascotaId);
        logger.debug("Se encontraron {} citas para la mascota ID: {}", citas.size(), mascotaId);
        return ResponseEntity.ok(citas);
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
        logger.info("Buscando citas para el cliente con ID: {}", clienteId);
        List<CitaDto> citas = citaService.getCitasByClienteId(clienteId);
        logger.debug("Se encontraron {} citas para el cliente ID: {}", citas.size(), clienteId);
        return ResponseEntity.ok(citas);
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
        logger.info("Buscando citas para el veterinario con ID: {}", veterinarioId);
        List<CitaDto> citas = citaService.getCitasByVeterinarioId(veterinarioId);
        logger.debug("Se encontraron {} citas para el veterinario ID: {}", citas.size(), veterinarioId);
        return ResponseEntity.ok(citas);
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
        logger.info("Buscando citas entre las fechas {} y {}", inicio, fin);
        List<CitaDto> citas = citaService.getCitasByFechaRango(inicio, fin);
        logger.debug("Se encontraron {} citas en el rango de fechas", citas.size());
        return ResponseEntity.ok(citas);
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
        logger.info("Buscando citas con estado: {}", estado);
        List<CitaDto> citas = citaService.getCitasByEstado(estado);
        logger.debug("Se encontraron {} citas en estado {}", citas.size(), estado);
        return ResponseEntity.ok(citas);
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
        logger.info("Obteniendo lista de próximas citas");
        List<CitaDto> citas = citaService.findProximasCitas();
        logger.debug("Se encontraron {} próximas citas", citas.size());
        return ResponseUtil.ok(citas);
    }
}
