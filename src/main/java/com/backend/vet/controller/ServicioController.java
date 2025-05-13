package com.backend.vet.controller;

import com.backend.vet.dto.ServicioDto;
import com.backend.vet.service.ServicioService;
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
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios Médicos", description = "API para la gestión de servicios médicos veterinarios")
public class ServicioController {
    
    private static final Logger logger = LoggerFactory.getLogger(ServicioController.class);
    
    @Autowired
    private ServicioService servicioService;
    
    @Operation(summary = "Obtener todos los servicios", description = "${api.servicio.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    public ResponseEntity<List<ServicioDto>> getAllServicios() {
        logger.info("Obteniendo lista de todos los servicios");
        List<ServicioDto> servicios = servicioService.getAllServicios();
        logger.debug("Se encontraron {} servicios", servicios.size());
        return ResponseUtil.ok(servicios);
    }
    
    @Operation(summary = "Obtener servicio por ID", description = "${api.servicio.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    public ResponseEntity<ServicioDto> getServicioById(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long id) {
        logger.info("Buscando servicio con ID: {}", id);
        ServicioDto servicio = servicioService.getServicioById(id);
        if (servicio != null) {
            logger.debug("Servicio encontrado: {}", servicio.getNombre());
            return ResponseUtil.ok(servicio);
        } else {
            logger.warn("No se encontró el servicio con ID: {}", id);
            return ResponseUtil.notFound();
        }
    }
    
    @Operation(summary = "Buscar servicios por nombre", description = "${api.servicio.getByNombre.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    public ResponseEntity<List<ServicioDto>> getServiciosByNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true)
            @RequestParam String nombre) {
        logger.info("Buscando servicios por nombre: {}", nombre);
        List<ServicioDto> servicios = servicioService.getServiciosByNombre(nombre);
        logger.debug("Se encontraron {} servicios con el nombre: {}", servicios.size(), nombre);
        return ResponseUtil.ok(servicios);
    }
    
    @Operation(summary = "Buscar servicios por precio máximo", description = "${api.servicio.getByPrecioMax.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/precio/hasta/{precio}")
    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    public ResponseEntity<List<ServicioDto>> getServiciosPrecioMenorIgual(
            @Parameter(description = "Precio máximo", required = true)
            @PathVariable BigDecimal precio) {
        logger.info("Buscando servicios con precio menor o igual a: {}", precio);
        List<ServicioDto> servicios = servicioService.getServiciosPrecioMenorIgual(precio);
        logger.debug("Se encontraron {} servicios con precio menor o igual a {}", servicios.size(), precio);
        return ResponseUtil.ok(servicios);
    }
    
    @Operation(summary = "Buscar servicios por precio mínimo", description = "${api.servicio.getByPrecioMin.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping("/precio/desde/{precio}")
    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    public ResponseEntity<List<ServicioDto>> getServiciosPrecioMayorIgual(
            @Parameter(description = "Precio mínimo", required = true)
            @PathVariable BigDecimal precio) {
        logger.info("Buscando servicios con precio mayor o igual a: {}", precio);
        List<ServicioDto> servicios = servicioService.getServiciosPrecioMayorIgual(precio);
        logger.debug("Se encontraron {} servicios con precio mayor o igual a {}", servicios.size(), precio);
        return ResponseUtil.ok(servicios);
    }
    
    @Operation(summary = "Crear un nuevo servicio", description = "${api.servicio.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SERVICIO_CREATE')")
    public ResponseEntity<ServicioDto> createServicio(
            @Parameter(description = "Datos del servicio", required = true)
            @Valid @RequestBody ServicioDto servicioDto) {
        logger.info("Creando nuevo servicio: {}", servicioDto.getNombre());
        ServicioDto createdServicio = servicioService.createServicio(servicioDto);
        logger.debug("Servicio creado con ID: {}", createdServicio.getId());
        return ResponseUtil.created(createdServicio);
    }
    
    @Operation(summary = "Actualizar servicio", description = "${api.servicio.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICIO_UPDATE')")
    public ResponseEntity<ServicioDto> updateServicio(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del servicio", required = true)
            @Valid @RequestBody ServicioDto servicioDto) {
        logger.info("Actualizando servicio con ID: {}", id);
        ServicioDto updatedServicio = servicioService.updateServicio(id, servicioDto);
        if (updatedServicio != null) {
            logger.debug("Servicio actualizado correctamente: {}", updatedServicio.getNombre());
            return ResponseUtil.ok(updatedServicio);
        } else {
            logger.warn("No se pudo actualizar el servicio con ID: {} - No encontrado", id);
            return ResponseUtil.notFound();
        }
    }
    
    @Operation(summary = "Eliminar servicio", description = "${api.servicio.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICIO_DELETE')")
    public ResponseEntity<Void> deleteServicio(
            @Parameter(description = "ID del servicio", required = true)
            @PathVariable Long id) {
        logger.info("Eliminando servicio con ID: {}", id);
        boolean deleted = servicioService.deleteServicio(id);
        if (deleted) {
            logger.debug("Servicio eliminado correctamente");
        } else {
            logger.warn("No se pudo eliminar el servicio con ID: {} - No encontrado", id);
        }
        return ResponseUtil.deleteResponse(deleted);
    }

    /**
     * Obtiene la lista de servicios veterinarios excluyendo peluquería
     */
    @Operation(summary = "Obtener servicios veterinarios", 
              description = "Proporciona la lista de servicios veterinarios excluyendo peluquería")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de servicios obtenida correctamente")
    })
    @GetMapping("/veterinarios")
    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    public ResponseEntity<List<ServicioDto>> getServiciosVeterinarios() {
        logger.info("Obteniendo lista de servicios veterinarios (excluyendo peluquería)");
        List<ServicioDto> servicios = servicioService.findAllExcluyendoPeluqueria();
        logger.debug("Se encontraron {} servicios veterinarios", servicios.size());
        return ResponseUtil.ok(servicios);
    }
}
