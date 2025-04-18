package com.backend.vet.security.services;

import com.backend.vet.model.Usuario;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));

        // Verificar si el usuario está activo
        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario no encontrado o inactivo: " + nombreUsuario);
            // O podrías lanzar una excepción específica como DisabledException si prefieres manejarla diferente
            // throw new DisabledException("Usuario inactivo: " + nombreUsuario);
        }

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasenaHash())
                // .disabled(!usuario.isActivo()) // Alternativa si no lanzas excepción arriba
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())))
                .build();
    }
}
