package com.backend.vet.controller;

import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.dto.UsuarioUpdateDto; // Importar el nuevo DTO
import com.backend.vet.service.UsuarioService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UsuarioController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Operation(summary = "Obtener todos los usuarios", description = "${api.usuario.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_READ')")
    public ResponseEntity<List<UsuarioDto>> getAllUsuarios() {
        logger.info("Obteniendo lista de todos los usuarios");
        List<UsuarioDto> usuarios = usuarioService.getAllUsuarios();
        logger.debug("Se encontraron {} usuarios", usuarios.size());
        return ResponseUtil.ok(usuarios);
    }
    
    @Operation(summary = "Obtener usuario por ID", description = "${api.usuario.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_READ') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UsuarioDto> getUsuarioById(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        UsuarioDto usuario = usuarioService.getUsuarioById(id);
        if (usuario != null) {
            logger.debug("Usuario encontrado con ID: {}", id);
            return ResponseUtil.ok(usuario);
        } else {
            logger.warn("No se encontró el usuario con ID: {}", id);
            return ResponseUtil.notFound();
        }
    }
    
    @Operation(summary = "Actualizar usuario", description = "${api.usuario.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_UPDATE') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UsuarioDto> updateUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @Valid @RequestBody UsuarioUpdateDto usuarioUpdateDto) { // Cambiar UsuarioDto a UsuarioUpdateDto
        logger.info("Actualizando usuario con ID: {}", id);
        UsuarioDto updatedUsuario = usuarioService.updateUsuario(id, usuarioUpdateDto); // Pasar el nuevo DTO al servicio
        if (updatedUsuario != null) {
            logger.debug("Usuario actualizado correctamente con ID: {}", id);
            return ResponseUtil.ok(updatedUsuario);
        } else {
            logger.warn("No se pudo actualizar el usuario con ID: {} - No encontrado", id);
            return ResponseUtil.notFound();
        }
    }
    
    @Operation(summary = "Eliminar usuario", description = "${api.usuario.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_DELETE')")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        boolean deleted = usuarioService.deleteUsuario(id);
        if (deleted) {
            logger.debug("Usuario eliminado correctamente");
        } else {
            logger.warn("No se pudo eliminar el usuario con ID: {} - No encontrado", id);
        }
        return ResponseUtil.deleteResponse(deleted);
    }

    @Operation(summary = "Obtener todos los veterinarios", description = "${api.usuario.getAllVeterinarios.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/veterinarios")
    @PreAuthorize("hasAuthority('USUARIO_READ')")
    public ResponseEntity<List<UsuarioDto>> getAllVeterinarios() {
        logger.info("Obteniendo lista de todos los veterinarios");
        List<UsuarioDto> veterinarios = usuarioService.getAllUsuarios().stream()
                .filter(u -> "VETERINARIO".equalsIgnoreCase(u.getRolNombre()))
                .collect(Collectors.toList());
        logger.debug("Se encontraron {} veterinarios", veterinarios.size());
        return ResponseUtil.ok(veterinarios);
    }

    @Operation(summary = "Obtener rol de un usuario", description = "Devuelve el nombre del rol asignado al usuario por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol encontrado",
                     content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}/rol")
    @PreAuthorize("hasAuthority('USUARIO_READ') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<String> getUsuarioRole(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id) {
        logger.info("Obteniendo rol del usuario con ID: {}", id);
        UsuarioDto usuario = usuarioService.getUsuarioById(id);
        if (usuario != null) {
            logger.debug("Rol del usuario {}: {}", id, usuario.getRolNombre());
            return ResponseUtil.ok(usuario.getRolNombre());
        } else {
            logger.warn("No se encontró el usuario con ID: {}", id);
            return ResponseUtil.notFound();
        }
    }

    @Operation(summary = "Obtener usuarios por nombre de rol", description = "Devuelve todos los usuarios que tienen el rol especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios encontrados"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/rol/{rolNombre}/usuarios")
    @PreAuthorize("hasAuthority('USUARIO_READ')")
    public ResponseEntity<List<UsuarioDto>> getUsuariosByRolNombre(
            @Parameter(description = "Nombre del rol", required = true)
            @PathVariable String rolNombre) {
        logger.info("Buscando usuarios con rol: {}", rolNombre);
        List<UsuarioDto> usuarios = usuarioService.getUsuariosByRolNombre(rolNombre);
        logger.debug("Se encontraron {} usuarios con rol {}", usuarios.size(), rolNombre);
        return ResponseUtil.ok(usuarios);
    }
}
