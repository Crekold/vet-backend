package com.backend.vet.controller;

import com.backend.vet.dto.PermissionDto;
import com.backend.vet.service.PermissionService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permisos", description = "API para la gestión de permisos del sistema")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Obtener todos los permisos", description = "Devuelve una lista de todos los permisos disponibles en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')") // Asegúrate de que exista este permiso o usa uno adecuado
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseUtil.ok(permissionService.getAllPermissions());
    }
}
