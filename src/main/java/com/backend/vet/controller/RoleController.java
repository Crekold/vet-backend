package com.backend.vet.controller;

import com.backend.vet.dto.RoleDto;
import com.backend.vet.service.RoleService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "API para la gestión de roles de usuarios")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @Operation(summary = "Obtener todos los roles", description = "${api.role.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}")
    })
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseUtil.ok(roleService.getAllRoles());
    }
    
    @Operation(summary = "Obtener rol por ID", description = "${api.role.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);
        return role != null ? ResponseUtil.ok(role) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Crear nuevo rol", description = "${api.role.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(
            @Parameter(description = "Datos del nuevo rol", required = true)
            @Valid @RequestBody RoleDto roleDto) {
        return ResponseUtil.created(roleService.createRole(roleDto));
    }
    
    @Operation(summary = "Actualizar rol", description = "${api.role.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> updateRole(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del rol", required = true)
            @Valid @RequestBody RoleDto roleDto) {
        RoleDto updatedRole = roleService.updateRole(id, roleDto);
        return updatedRole != null ? ResponseUtil.ok(updatedRole) : ResponseUtil.notFound();
    }
    
    @Operation(summary = "Eliminar rol", description = "${api.role.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(roleService.deleteRole(id));
    }
}
