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
        return ResponseUtil.ok(archivoClinicoService.getAllArchivosClinico());
    }
    
    @Operation(summary = "Obtener archivo clínico por ID", description = "${api.archivoClinico.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArchivoClinicoDto> getArchivoClinicoById(
            @Parameter(description = "ID del archivo clínico", required = true)
            @PathVariable Long id) {
        ArchivoClinicoDto archivoClinico = archivoClinicoService.getArchivoClinicoById(id);
        return archivoClinico != null ? ResponseUtil.ok(archivoClinico) : ResponseUtil.notFound();
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
        try {
            ArchivoClinicoDto resultado = archivoClinicoService.createArchivoClinico(file, historialClinicoId);
            return ResponseUtil.ok(resultado);
        } catch (FileStorageException e) {
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
        ArchivoClinicoDto updatedArchivoClinico = archivoClinicoService.updateArchivoClinico(id, archivoClinicoDto);
        return updatedArchivoClinico != null ? ResponseUtil.ok(updatedArchivoClinico) : ResponseUtil.notFound();
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
        return ResponseUtil.deleteResponse(archivoClinicoService.deleteArchivoClinico(id));
    }
    
    @Operation(summary = "Descargar archivo clínico", description = "${api.archivoClinico.download.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Nombre del archivo", required = true)
            @PathVariable String fileName) {
        try {
            // Carga el archivo como un recurso
            Resource resource = archivoClinicoService.loadFileAsResource(fileName);
            
            // Determina el tipo de contenido (si es posible)
            String contentType = "application/octet-stream";
            String originalFileName = fileName;
            
            // Extraer el nombre original si es un archivo con UUID_nombre.ext
            if (fileName.contains("_")) {
                originalFileName = fileName.substring(fileName.indexOf("_") + 1);
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                    .body(resource);
        } catch (FileStorageException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Obtener archivos por historial clínico", description = "${api.archivoClinico.getByHistorial.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/historial/{historialClinicoId}")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByHistorialClinicoId(
            @Parameter(description = "ID del historial clínico", required = true)
            @PathVariable Long historialClinicoId) {
        return ResponseUtil.ok(archivoClinicoService.getArchivosByHistorialClinicoId(historialClinicoId));
    }
    
    @Operation(summary = "Obtener archivos por mascota", description = "${api.archivoClinico.getByMascota.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByMascotaId(
            @Parameter(description = "ID de la mascota", required = true)
            @PathVariable Long mascotaId) {
        return ResponseUtil.ok(archivoClinicoService.getArchivosByMascotaId(mascotaId));
    }
    
    @Operation(summary = "Buscar archivos por nombre", description = "${api.archivoClinico.getByNombre.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByNombre(
            @Parameter(description = "Texto a buscar en el nombre de archivo", required = true)
            @RequestParam String nombre) {
        return ResponseUtil.ok(archivoClinicoService.getArchivosByNombre(nombre));
    }
    
    @Operation(summary = "Obtener archivos por tipo MIME", description = "${api.archivoClinico.getByTipoMime.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/tipo/{tipoMime}")
    public ResponseEntity<List<ArchivoClinicoDto>> getArchivosByTipoMime(
            @Parameter(description = "Tipo MIME del archivo", required = true)
            @PathVariable String tipoMime) {
        return ResponseUtil.ok(archivoClinicoService.getArchivosByTipoMime(tipoMime));
    }
}
