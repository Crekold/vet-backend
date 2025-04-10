package com.backend.vet.service;

import com.backend.vet.model.Role;
import com.backend.vet.model.Usuario;
import com.backend.vet.repository.RoleRepository;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitService implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setNombre("ADMIN");
            roleRepository.save(adminRole);
            
            Role userRole = new Role();
            userRole.setNombre("USER");
            roleRepository.save(userRole);
            
            // Crear usuario admin por defecto
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNombreUsuario("admin");
                admin.setCorreo("admin@example.com");
                admin.setContrasenaHash(passwordEncoder.encode("admin"));
                admin.setRol(adminRole);
                usuarioRepository.save(admin);
            }
        }
    }
}
