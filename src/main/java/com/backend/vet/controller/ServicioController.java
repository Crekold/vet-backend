package com.backend.vet.controller;

import com.backend.vet.dto.ServicioDto;
import com.backend.vet.service.ServicioService;
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
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios Médicos", description = "API para la gestión de servicios médicos veterinarios")
public class ServicioController {
    
    @Autowired
    private ServicioService servicioService;
    
    @Operation(summary = "Obtener todos los servicios", description = "${api.servicio.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping
    public ResponseEntity<List<ServicioDto>> getAllServicios() {
        return ResponseUtil.ok(servicioService.getAllServicios());
    }
    
    @Operation(summary = "Obtener servicio por ID", description = "${api.servicio.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServicioDto> getServicioById(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long id) {
        ServicioDto servicio = servicioService.getServicioById(id);
        return servicio != null ? ResponseUtil.ok(servicio) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Buscar servicios por nombre", description = "${api.servicio.getByNombre.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<ServicioDto>> getServiciosByNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true)
            @RequestParam String nombre) {
        return ResponseUtil.ok(servicioService.getServiciosByNombre(nombre));
    }
    
    @Operation(summary = "Buscar servicios por precio máximo", description = "${api.servicio.getByPrecioMax.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/precio/hasta/{precio}")
    public ResponseEntity<List<ServicioDto>> getServiciosPrecioMenorIgual(
            @Parameter(description = "Precio máximo", required = true)
            @PathVariable BigDecimal precio) {
        return ResponseUtil.ok(servicioService.getServiciosPrecioMenorIgual(precio));
    }
    
    @Operation(summary = "Buscar servicios por precio mínimo", description = "${api.servicio.getByPrecioMin.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/precio/desde/{precio}")
    public ResponseEntity<List<ServicioDto>> getServiciosPrecioMayorIgual(
            @Parameter(description = "Precio mínimo", required = true)
            @PathVariable BigDecimal precio) {
        return ResponseUtil.ok(servicioService.getServiciosPrecioMayorIgual(precio));
    }
    
    @Operation(summary = "Crear un nuevo servicio", description = "${api.servicio.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioDto> createServicio(
            @Parameter(description = "Datos del servicio", required = true)
            @Valid @RequestBody ServicioDto servicioDto) {
        return ResponseUtil.created(servicioService.createServicio(servicioDto));
    }
    
    @Operation(summary = "Actualizar servicio", description = "${api.servicio.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioDto> updateServicio(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del servicio", required = true)
            @Valid @RequestBody ServicioDto servicioDto) {
        ServicioDto updatedServicio = servicioService.updateServicio(id, servicioDto);
        return updatedServicio != null ? ResponseUtil.ok(updatedServicio) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Eliminar servicio", description = "${api.servicio.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteServicio(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(servicioService.deleteServicio(id));
    }
}
