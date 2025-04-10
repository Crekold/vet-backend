package com.backend.vet.service;

import com.backend.vet.dto.RoleDto;
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Role;
import com.backend.vet.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public RoleDto getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public Optional<RoleDto> getRoleByNombre(String nombre) {
        return roleRepository.findByNombre(nombre).map(this::convertToDto);
    }
    
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        if (roleRepository.existsByNombre(roleDto.getNombre())) {
            throw new BadRequestException("Ya existe un rol con el nombre: " + roleDto.getNombre());
        }
        
        Role role = new Role();
        role.setNombre(roleDto.getNombre());
        
        Role savedRole = roleRepository.save(role);
        return convertToDto(savedRole);
    }
    
    @Transactional
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
            
            if (!role.getNombre().equals(roleDto.getNombre()) && 
                roleRepository.existsByNombre(roleDto.getNombre())) {
                throw new BadRequestException("Ya existe un rol con el nombre: " + roleDto.getNombre());
            }
            
            role.setNombre(roleDto.getNombre());
            Role updatedRole = roleRepository.save(role);
            return convertToDto(updatedRole);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
        return true;
    }
    
    private RoleDto convertToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setNombre(role.getNombre());
        return dto;
    }
    
    private Role convertToEntity(RoleDto roleDto) {
        Role role = new Role();
        if (roleDto.getId() != null) {
            role.setId(roleDto.getId());
        }
        role.setNombre(roleDto.getNombre());
        return role;
    }
}
