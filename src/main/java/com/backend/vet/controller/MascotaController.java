package com.backend.vet.controller;

import com.backend.vet.dto.MascotaDto;
import com.backend.vet.service.MascotaService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    
    @Autowired
    private MascotaService mascotaService;
    
    @Operation(summary = "Obtener todas las mascotas", description = "${api.mascota.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')") // Solo Admin y Empleado pueden ver TODAS las mascotas
    public ResponseEntity<List<MascotaDto>> getAllMascotas() {
        return ResponseUtil.ok(mascotaService.getAllMascotas());
    }
    
    @Operation(summary = "Obtener mascota por ID", description = "${api.mascota.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'EMPLEADO')") // Permitir lectura a los tres roles
    public ResponseEntity<MascotaDto> getMascotaById(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long id) {
        MascotaDto mascota = mascotaService.getMascotaById(id);
        return mascota != null ? ResponseUtil.ok(mascota) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Obtener mascotas por cliente", description = "${api.mascota.getByCliente.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'EMPLEADO')") // Permitir lectura a los tres roles
    public ResponseEntity<List<MascotaDto>> getMascotasByClienteId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        return ResponseUtil.ok(mascotaService.getMascotasByClienteId(clienteId));
    }
    
    @Operation(summary = "Crear nueva mascota", description = "${api.mascota.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<MascotaDto> createMascota(
            @Parameter(description = "Datos de la nueva mascota", required = true)
            @Valid @RequestBody MascotaDto mascotaDto) {
        return ResponseUtil.created(mascotaService.createMascota(mascotaDto));
    }
    
    @Operation(summary = "Actualizar mascota", description = "${api.mascota.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<MascotaDto> updateMascota(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados de la mascota", required = true)
            @Valid @RequestBody MascotaDto mascotaDto) {
        MascotaDto updatedMascota = mascotaService.updateMascota(id, mascotaDto);
        return updatedMascota != null ? ResponseUtil.ok(updatedMascota) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Eliminar mascota", description = "${api.mascota.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Void> deleteMascota(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(mascotaService.deleteMascota(id));
    }
}
