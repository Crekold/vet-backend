package com.backend.vet.controller;

import com.backend.vet.dto.PermissionDto;
import com.backend.vet.dto.RoleDto;
import com.backend.vet.service.RoleService;
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
import java.util.List;
import java.util.Set;

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
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseUtil.ok(roleService.getAllRoles());
    }
    
    @Operation(summary = "Obtener rol por ID", description = "${api.role.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
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
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
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
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
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
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable Long id) {
        return ResponseUtil.deleteResponse(roleService.deleteRole(id));
    }

    @Operation(summary = "Obtener permisos de un rol", description = "Listar permisos asignados al rol")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<List<PermissionDto>> getPermissionsByRole(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable Long id) {
        List<PermissionDto> perms = roleService.getPermissionsByRole(id);
        return ResponseUtil.ok(perms);
    }

    @Operation(summary = "Actualizar todos los permisos de un rol", description = "Reemplaza todos los permisos existentes de un rol con el conjunto proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<List<PermissionDto>> updatePermissions(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable Long id,
            @Parameter(description = "Conjunto de IDs de los permisos a asignar", required = true)
            @RequestBody Set<Long> permissionIds) {
        List<PermissionDto> updated = roleService.updatePermissions(id, permissionIds);
        return ResponseUtil.ok(updated);
    }

    @Operation(summary = "Asignar un permiso específico a un rol", description = "Añade un permiso individual a un rol si aún no está asignado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso asignado correctamente. Devuelve la lista actualizada de permisos."),
        @ApiResponse(responseCode = "404", description = "Rol o permiso no encontrado."),
        @ApiResponse(responseCode = "400", description = "El permiso ya estaba asignado o error en la solicitud."),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<List<PermissionDto>> assignPermissionToRole(
            @Parameter(description = "ID del rol", required = true) @PathVariable Long roleId,
            @Parameter(description = "ID del permiso a asignar", required = true) @PathVariable Long permissionId) {
        List<PermissionDto> updatedPermissions = roleService.assignPermissionToRole(roleId, permissionId);
        return ResponseUtil.ok(updatedPermissions);
    }

    @Operation(summary = "Revocar un permiso específico de un rol", description = "Elimina un permiso individual de un rol.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Permiso revocado correctamente."),
        @ApiResponse(responseCode = "404", description = "Rol o permiso no encontrado, o el permiso no estaba asignado al rol."),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<Void> removePermissionFromRole(
            @Parameter(description = "ID del rol", required = true) @PathVariable Long roleId,
            @Parameter(description = "ID del permiso a revocar", required = true) @PathVariable Long permissionId) {
        boolean removed = roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseUtil.deleteResponse(removed);
    }
}
