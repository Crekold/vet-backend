package com.backend.vet.service;

import com.backend.vet.dto.PermissionDto;
import com.backend.vet.model.Permission;
import com.backend.vet.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Obtiene todos los permisos disponibles en el sistema.
     * @return Lista de PermissionDto.
     */
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PermissionDto convertToDto(Permission permission) {
        // Nota: El modelo Permission actual no tiene 'description'.
        // Si se añade al modelo, descomentar la línea correspondiente.
        return new PermissionDto(
                permission.getId(),
                permission.getName()
                //, permission.getDescription() // Descomentar si se añade description al modelo
        );
    }
}
