package com.backend.vet.service;

import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Role;
import com.backend.vet.model.Usuario;
import com.backend.vet.repository.RoleRepository;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public UsuarioDto getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public Optional<UsuarioDto> getUsuarioByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(this::convertToDto);
    }
    
    @Transactional
    public UsuarioDto createUsuario(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByNombreUsuario(usuarioDto.getNombreUsuario())) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByCorreo(usuarioDto.getCorreo())) {
            throw new BadRequestException("El correo electrónico ya está registrado");
        }
        
        Usuario usuario = new Usuario();
        
        usuario.setNombreUsuario(usuarioDto.getNombreUsuario());
        usuario.setCorreo(usuarioDto.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(usuarioDto.getContrasena()));
        usuario.setEspecialidad(usuarioDto.getEspecialidad());
        
        Role role = roleRepository.findById(usuarioDto.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", usuarioDto.getRolId()));
        usuario.setRol(role);
        
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDto(savedUsuario);
    }
    
    @Transactional
    public UsuarioDto updateUsuario(Long id, UsuarioDto usuarioDto) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
            
            // Verificar si el nuevo nombreUsuario ya existe y no pertenece a este usuario
            if (!usuario.getNombreUsuario().equals(usuarioDto.getNombreUsuario()) && 
                usuarioRepository.existsByNombreUsuario(usuarioDto.getNombreUsuario())) {
                throw new BadRequestException("El nombre de usuario ya está en uso");
            }
            
            // Verificar si el nuevo correo ya existe y no pertenece a este usuario
            if (!usuario.getCorreo().equals(usuarioDto.getCorreo()) && 
                usuarioRepository.existsByCorreo(usuarioDto.getCorreo())) {
                throw new BadRequestException("El correo electrónico ya está registrado");
            }
            
            usuario.setNombreUsuario(usuarioDto.getNombreUsuario());
            usuario.setCorreo(usuarioDto.getCorreo());
            usuario.setEspecialidad(usuarioDto.getEspecialidad());
            
            if (usuarioDto.getContrasena() != null && !usuarioDto.getContrasena().isEmpty()) {
                usuario.setContrasenaHash(passwordEncoder.encode(usuarioDto.getContrasena()));
            }
            
            if (usuarioDto.getRolId() != null) {
                Role role = roleRepository.findById(usuarioDto.getRolId())
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", usuarioDto.getRolId()));
                usuario.setRol(role);
            }
            
            Usuario updatedUsuario = usuarioRepository.save(usuario);
            return convertToDto(updatedUsuario);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
        return true;
    }
    
    private UsuarioDto convertToDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setCorreo(usuario.getCorreo());
        dto.setEspecialidad(usuario.getEspecialidad());
        // No establecemos la contraseña, ya que @JsonProperty(access = WRITE_ONLY) 
        // impedirá que se incluya en la respuesta
        
        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getId());
            dto.setRolNombre(usuario.getRol().getNombre());
        }
        
        return dto;
    }
}
