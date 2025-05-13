package com.backend.vet.controller;

import com.backend.vet.dto.MascotaDto;
import com.backend.vet.service.MascotaService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@Tag(name = "Mascotas", description = "API para la gestión de mascotas")
public class MascotaController {
    
    private static final Logger logger = LoggerFactory.getLogger(MascotaController.class);
    
    @Autowired
    private MascotaService mascotaService;
    
    @Operation(summary = "Obtener todas las mascotas", description = "${api.mascota.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('MASCOTA_READ')")
    public ResponseEntity<List<MascotaDto>> getAllMascotas() {
        logger.info("Consultando todas las mascotas");
        List<MascotaDto> mascotas = mascotaService.getAllMascotas();
        logger.debug("Se encontraron {} mascotas", mascotas.size());
        return ResponseUtil.ok(mascotas);
    }
    
    @Operation(summary = "Obtener mascota por ID", description = "${api.mascota.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MASCOTA_READ')")
    public ResponseEntity<MascotaDto> getMascotaById(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long id) {
        logger.info("Buscando mascota con ID: {}", id);
        MascotaDto mascota = mascotaService.getMascotaById(id);
        if (mascota == null) {
            logger.warn("No se encontró la mascota con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.debug("Mascota encontrada con ID: {}", id);
        return ResponseUtil.ok(mascota);
    }
    
    @Operation(summary = "Obtener mascotas por cliente", description = "${api.mascota.getByCliente.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('MASCOTA_READ')")
    public ResponseEntity<List<MascotaDto>> getMascotasByClienteId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        logger.info("Consultando mascotas del cliente con ID: {}", clienteId);
        List<MascotaDto> mascotas = mascotaService.getMascotasByClienteId(clienteId);
        logger.debug("Se encontraron {} mascotas para el cliente ID: {}", mascotas.size(), clienteId);
        return ResponseUtil.ok(mascotas);
    }
    
    @Operation(summary = "Crear nueva mascota", description = "${api.mascota.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('MASCOTA_CREATE')")
    public ResponseEntity<MascotaDto> createMascota(
            @Parameter(description = "Datos de la nueva mascota", required = true)
            @Valid @RequestBody MascotaDto mascotaDto) {
        logger.info("Creando nueva mascota para el cliente ID: {}", mascotaDto.getClienteId());
        try {
            MascotaDto created = mascotaService.createMascota(mascotaDto);
            logger.info("Mascota creada exitosamente con ID: {}", created.getId());
            return ResponseUtil.created(created);
        } catch (Exception e) {
            logger.error("Error al crear mascota: {}", e.getMessage());
            throw e;
        }
    }
    
    @Operation(summary = "Actualizar mascota", description = "${api.mascota.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MASCOTA_UPDATE')")
    public ResponseEntity<MascotaDto> updateMascota(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados de la mascota", required = true)
            @Valid @RequestBody MascotaDto mascotaDto) {
        logger.info("Actualizando mascota con ID: {}", id);
        MascotaDto updatedMascota = mascotaService.updateMascota(id, mascotaDto);
        if (updatedMascota == null) {
            logger.warn("No se pudo actualizar la mascota con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Mascota actualizada exitosamente con ID: {}", id);
        return ResponseUtil.ok(updatedMascota);
    }
    
    @Operation(summary = "Eliminar mascota", description = "${api.mascota.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MASCOTA_DELETE')")
    public ResponseEntity<Void> deleteMascota(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long id) {
        logger.info("Eliminando mascota con ID: {}", id);
        boolean deleted = mascotaService.deleteMascota(id);
        if (!deleted) {
            logger.warn("No se pudo eliminar la mascota con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Mascota eliminada exitosamente con ID: {}", id);
        return ResponseUtil.deleteResponse(deleted);
    }
}
