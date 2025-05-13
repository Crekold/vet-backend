package com.backend.vet.controller;

import com.backend.vet.dto.ArchivoClinicoDto;
import com.backend.vet.exception.FileStorageException;
import com.backend.vet.service.ArchivoClinicoService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/archivos-clinicos")
@Tag(name = "Archivos Clínicos", description = "API para la gestión de archivos adjuntos al historial clínico")
public class ArchivoClinicoController {
    
    private static final Logger logger = LoggerFactory.getLogger(ArchivoClinicoController.class);
    
    @Autowired
    private ArchivoClinicoService archivoClinicoService;
    
    @Operation(summary = "Obtener todos los archivos clínicos", description = "${api.archivoClinico.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIO')")
    public ResponseEntity<List<ArchivoClinicoDto>> getAllArchivosClinico() {
        logger.info("Obteniendo todos los archivos clínicos");
        List<ArchivoClinicoDto> archivos = archivoClinicoService.getAllArchivosClinico();
        logger.debug("Se encontraron {} archivos clínicos", archivos.size());
        return ResponseUtil.ok(archivos);
    }
    
    @Operation(summary = "Obtener archivo clínico por ID", description = "${api.archivoClinico.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<ArchivoClinicoDto> getArchivoClinicoById(
            @Parameter(description = "ID del archivo clínico", required = true)
            @PathVariable Long id) {
        logger.info("Buscando archivo clínico con ID: {}", id);
        ArchivoClinicoDto archivoClinico = archivoClinicoService.getArchivoClinicoById(id);
        if (archivoClinico == null) {
            logger.warn("No se encontró el archivo clínico con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.debug("Archivo clínico encontrado con ID: {}", id);
        return ResponseUtil.ok(archivoClinico);
    }
    
    @Operation(summary = "Subir archivo clínico", description = "${api.archivoClinico.upload.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VETERINARIO') or hasRole('ADMIN')")
    public ResponseEntity<ArchivoClinicoDto> uploadArchivoClinico(
            @Parameter(description = "Archivo a cargar", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID del historial clínico", required = true)
            @RequestParam("historialClinicoId") Long historialClinicoId) {
        logger.info("Subiendo archivo para historial clínico ID: {}, nombre del archivo: {}", 
                   historialClinicoId, file.getOriginalFilename());
        try {
            ArchivoClinicoDto resultado = archivoClinicoService.createArchivoClinico(file, historialClinicoId);
            logger.info("Archivo subido exitosamente con ID: {}", resultado.getId());
            return ResponseUtil.ok(resultado);
        } catch (FileStorageException e) {
            logger.error("Error al subir archivo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Actualizar datos de archivo clínico", description = "${api.archivoClinico.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARIO') or hasRole('ADMIN')")
    public ResponseEntity<ArchivoClinicoDto> updateArchivoClinico(
            @Parameter(description = "ID del archivo clínico", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del archivo", required = true)
            @Valid @RequestBody ArchivoClinicoDto archivoClinicoDto) {
        logger.info("Actualizando archivo clínico con ID: {}", id);
        ArchivoClinicoDto updatedArchivoClinico = archivoClinicoService.updateArchivoClinico(id, archivoClinicoDto);
        if (updatedArchivoClinico == null) {
            logger.warn("No se pudo actualizar el archivo clínico con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Archivo clínico actualizado exitosamente con ID: {}", id);
        return ResponseUtil.ok(updatedArchivoClinico);
    }
    
    @Operation(summary = "Eliminar archivo clínico", description = "${api.archivoClinico.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArchivoClinico(
            @Parameter(description = "ID del archivo clínico", required = true)
            @PathVariable Long id) {
        logger.info("Eliminando archivo clínico con ID: {}", id);
        boolean deleted = archivoClinicoService.deleteArchivoClinico(id);
        if (!deleted) {
            logger.warn("No se pudo eliminar el archivo clínico con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Archivo clínico eliminado exitosamente con ID: {}", id);
        return ResponseUtil.deleteResponse(true);
    }
    
    @Operation(summary = "Descargar archivo clínico", description = "${api.archivoClinico.download.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/download/{fileName:.+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Nombre del archivo", required = true)
            @PathVariable String fileName) {
        logger.info("Solicitando descarga del archivo: {}", fileName);
        try {
            Resource resource = archivoClinicoService.loadFileAsResource(fileName);
            String originalFileName = fileName;
            if (fileName.contains("_")) {
                originalFileName = fileName.substring(fileName.indexOf("_") + 1);
            }
            logger.debug("Archivo encontrado y listo para descarga: {}", originalFileName);
            
            String contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                    .body(resource);
        } catch (FileStorageException ex) {
            logger.error("Error al intentar descargar el archivo {}: {}", fileName, ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Obtener archivos por historial clínico", description = "${api.archivoClinico.getByHistorial.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/historial/{historialClinicoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByHistorialClinicoId(
            @Parameter(description = "ID del historial clínico", required = true)
            @PathVariable Long historialClinicoId) {
        logger.info("Buscando archivos del historial clínico ID: {}", historialClinicoId);
        List<ArchivoClinicoDto> archivos = archivoClinicoService.getArchivosByHistorialClinicoId(historialClinicoId);
        logger.debug("Se encontraron {} archivos para el historial clínico ID: {}", archivos.size(), historialClinicoId);
        return ResponseUtil.ok(archivos);
    }
    
    @Operation(summary = "Obtener archivos por mascota", description = "${api.archivoClinico.getByMascota.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/mascota/{mascotaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByMascotaId(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long mascotaId) {
        logger.info("Buscando archivos de la mascota ID: {}", mascotaId);
        List<ArchivoClinicoDto> archivos = archivoClinicoService.getArchivosByMascotaId(mascotaId);
        logger.debug("Se encontraron {} archivos para la mascota ID: {}", archivos.size(), mascotaId);
        return ResponseUtil.ok(archivos);
    }
    
    @Operation(summary = "Buscar archivos por nombre", description = "${api.archivoClinico.getByNombre.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByNombre(
            @Parameter(description = "Texto a buscar en el nombre de archivo", required = true)
            @RequestParam String nombre) {
        logger.info("Buscando archivos que contengan en el nombre: {}", nombre);
        List<ArchivoClinicoDto> archivos = archivoClinicoService.getArchivosByNombre(nombre);
        logger.debug("Se encontraron {} archivos que coinciden con el nombre: {}", archivos.size(), nombre);
        return ResponseUtil.ok(archivos);
    }
    
    @Operation(summary = "Obtener archivos por tipo MIME", description = "${api.archivoClinico.getByTipoMime.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/tipo/{tipoMime}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByTipoMime(
            @Parameter(description = "Tipo MIME del archivo", required = true)
            @PathVariable String tipoMime) {
        logger.info("Buscando archivos por tipo MIME: {}", tipoMime);
        List<ArchivoClinicoDto> archivos = archivoClinicoService.getArchivosByTipoMime(tipoMime);
        logger.debug("Se encontraron {} archivos del tipo MIME: {}", archivos.size(), tipoMime);
        return ResponseUtil.ok(archivos);
    }
}
