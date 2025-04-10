package com.backend.vet.security;

import com.backend.vet.model.Usuario;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        return usuarioRepository.findById(userId)
                .map(user -> user.getNombreUsuario().equals(currentUsername))
                .orElse(false);
    }
}
