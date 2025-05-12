package com.backend.vet.config;

import com.backend.vet.model.Permission;
import com.backend.vet.model.Role;
import com.backend.vet.repository.PermissionRepository;
import com.backend.vet.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }    @Override
    @Transactional
    public void run(String... args) {
        // 1. Crear permisos según matriz
        List<String> allPermNames = Arrays.asList(
            "AUTH_ACCESS",
            "USUARIO_CREATE","USUARIO_READ","USUARIO_UPDATE","USUARIO_DELETE",
            "ROLE_CREATE","ROLE_READ","ROLE_UPDATE","ROLE_DELETE",
            "PERMISSION_CREATE","PERMISSION_READ","PERMISSION_UPDATE","PERMISSION_DELETE",
            "CLIENTE_CREATE","CLIENTE_READ","CLIENTE_UPDATE","CLIENTE_DELETE",
            "MASCOTA_CREATE","MASCOTA_READ","MASCOTA_UPDATE","MASCOTA_DELETE",
            "HISTORIAL_CLINICO_CREATE","HISTORIAL_CLINICO_READ","HISTORIAL_CLINICO_UPDATE","HISTORIAL_CLINICO_DELETE",
            "ARCHIVO_CLINICO_CREATE","ARCHIVO_CLINICO_READ","ARCHIVO_CLINICO_UPDATE","ARCHIVO_CLINICO_DELETE",
            "CITA_CREATE","CITA_READ","CITA_UPDATE","CITA_DELETE",
            "CITA_SERVICIO_CREATE","CITA_SERVICIO_READ","CITA_SERVICIO_UPDATE","CITA_SERVICIO_DELETE",
            "SERVICIO_CREATE","SERVICIO_READ","SERVICIO_UPDATE","SERVICIO_DELETE",
            "STATS_CREATE","STATS_READ","STATS_UPDATE","STATS_DELETE"
        );
        List<Permission> allPerms = new ArrayList<>();
        for (String name : allPermNames) {
            Permission perm = permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name)));
            allPerms.add(perm);
        }

        // 2. Crear roles granulares
        Map<String, Role> roles = new HashMap<>();
        
        // Creación/recuperación de todos los roles
        String[] roleNames = {
            "ADMIN_SISTEMA",           // Administrador del sistema (configuración técnica)
            "ADMIN_USUARIOS",          // Gestión de usuarios y permisos
            "ADMIN_VETERINARIA",       // Administración general de la veterinaria
            "GERENTE",                 // Gerente con acceso a estadísticas y reportes
            "VETERINARIO_SENIOR",      // Veterinario con todos los permisos clínicos
            "VETERINARIO_JUNIOR",      // Veterinario con permisos clínicos limitados
            "RECEPCIONISTA",           // Manejo de citas y clientes
            "ASISTENTE",               // Asistente con acceso limitado
            "CONSULTOR_DATOS"          // Solo lectura para análisis
        };
        
        for (String roleName : roleNames) {
            Role role = roleRepository.findByNombre(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName, new HashSet<>())));
            roles.put(roleName, role);
        }
        
        // Mantenemos roles anteriores por compatibilidad
        Role adminLegacy = roleRepository.findByNombre("ADMIN").orElseGet(() -> roleRepository.save(new Role(null, "ADMIN", new HashSet<>())));
        Role vetLegacy = roleRepository.findByNombre("VETERINARIO").orElseGet(() -> roleRepository.save(new Role(null, "VETERINARIO", new HashSet<>())));
        Role empLegacy = roleRepository.findByNombre("EMPLEADO").orElseGet(() -> roleRepository.save(new Role(null, "EMPLEADO", new HashSet<>())));
        
        // 3. Asignar permisos a los roles según su función
        
        // ADMIN_SISTEMA: Configuración técnica
        Set<String> adminSistemaPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS",
            "USUARIO_CREATE", "USUARIO_READ", "USUARIO_UPDATE", "USUARIO_DELETE",
            "ROLE_CREATE", "ROLE_READ", "ROLE_UPDATE", "ROLE_DELETE",
            "PERMISSION_CREATE", "PERMISSION_READ", "PERMISSION_UPDATE", "PERMISSION_DELETE"
        ));
        roles.get("ADMIN_SISTEMA").setPermissions(allPerms.stream()
            .filter(p -> adminSistemaPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("ADMIN_SISTEMA"));
        
        // ADMIN_USUARIOS: Gestión de usuarios
        Set<String> adminUsuariosPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS",
            "USUARIO_CREATE", "USUARIO_READ", "USUARIO_UPDATE", "USUARIO_DELETE",
            "ROLE_READ"
        ));
        roles.get("ADMIN_USUARIOS").setPermissions(allPerms.stream()
            .filter(p -> adminUsuariosPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("ADMIN_USUARIOS"));
        
        // ADMIN_VETERINARIA: Administración general de la veterinaria
        Set<String> adminVeterinariaPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS", "USUARIO_READ",
            "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE", "CLIENTE_DELETE",
            "MASCOTA_CREATE", "MASCOTA_READ", "MASCOTA_UPDATE", "MASCOTA_DELETE",
            "HISTORIAL_CLINICO_READ", "HISTORIAL_CLINICO_DELETE",
            "ARCHIVO_CLINICO_READ", "ARCHIVO_CLINICO_DELETE",
            "CITA_CREATE", "CITA_READ", "CITA_UPDATE", "CITA_DELETE",
            "SERVICIO_CREATE", "SERVICIO_READ", "SERVICIO_UPDATE", "SERVICIO_DELETE", 
            "STATS_READ"
        ));
        roles.get("ADMIN_VETERINARIA").setPermissions(allPerms.stream()
            .filter(p -> adminVeterinariaPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("ADMIN_VETERINARIA"));
        
        // GERENTE: Gestión y estadísticas
        Set<String> gerentePerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS", "USUARIO_READ",
            "CLIENTE_READ", "MASCOTA_READ",
            "HISTORIAL_CLINICO_READ",
            "CITA_READ", "CITA_UPDATE",
            "SERVICIO_READ", "SERVICIO_UPDATE",
            "STATS_CREATE", "STATS_READ", "STATS_UPDATE"
        ));
        roles.get("GERENTE").setPermissions(allPerms.stream()
            .filter(p -> gerentePerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("GERENTE"));
        
        // VETERINARIO_SENIOR: Veterinario con todos los permisos clínicos
        Set<String> vetSeniorPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS", "USUARIO_READ",
            "CLIENTE_READ", "CLIENTE_UPDATE",
            "MASCOTA_CREATE", "MASCOTA_READ", "MASCOTA_UPDATE",
            "HISTORIAL_CLINICO_CREATE", "HISTORIAL_CLINICO_READ", "HISTORIAL_CLINICO_UPDATE",
            "ARCHIVO_CLINICO_CREATE", "ARCHIVO_CLINICO_READ", "ARCHIVO_CLINICO_UPDATE",
            "CITA_READ", "CITA_UPDATE",
            "CITA_SERVICIO_CREATE", "CITA_SERVICIO_READ", "CITA_SERVICIO_UPDATE",
            "SERVICIO_READ", "STATS_READ"
        ));
        roles.get("VETERINARIO_SENIOR").setPermissions(allPerms.stream()
            .filter(p -> vetSeniorPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("VETERINARIO_SENIOR"));
        
        // VETERINARIO_JUNIOR: Veterinario con permisos clínicos limitados
        Set<String> vetJuniorPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS",
            "CLIENTE_READ",
            "MASCOTA_READ",
            "HISTORIAL_CLINICO_CREATE", "HISTORIAL_CLINICO_READ",
            "ARCHIVO_CLINICO_CREATE", "ARCHIVO_CLINICO_READ",
            "CITA_READ",
            "CITA_SERVICIO_CREATE", "CITA_SERVICIO_READ",
            "SERVICIO_READ"
        ));
        roles.get("VETERINARIO_JUNIOR").setPermissions(allPerms.stream()
            .filter(p -> vetJuniorPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("VETERINARIO_JUNIOR"));
        
        // RECEPCIONISTA: Manejo de citas y clientes
        Set<String> recepcionistaPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS",
            "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE",
            "MASCOTA_CREATE", "MASCOTA_READ", "MASCOTA_UPDATE",
            "CITA_CREATE", "CITA_READ", "CITA_UPDATE",
            "CITA_SERVICIO_CREATE", "CITA_SERVICIO_READ", "CITA_SERVICIO_UPDATE",
            "SERVICIO_READ"
        ));
        roles.get("RECEPCIONISTA").setPermissions(allPerms.stream()
            .filter(p -> recepcionistaPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("RECEPCIONISTA"));
        
        // ASISTENTE: Asistente con acceso limitado
        Set<String> asistentePerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS",
            "CLIENTE_READ",
            "MASCOTA_READ",
            "CITA_READ", 
            "SERVICIO_READ"
        ));
        roles.get("ASISTENTE").setPermissions(allPerms.stream()
            .filter(p -> asistentePerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("ASISTENTE"));
        
        // CONSULTOR_DATOS: Solo lectura para análisis
        Set<String> consultorPerms = new HashSet<>(Arrays.asList(
            "AUTH_ACCESS",
            "CLIENTE_READ", "MASCOTA_READ",
            "HISTORIAL_CLINICO_READ", "ARCHIVO_CLINICO_READ",
            "CITA_READ", "CITA_SERVICIO_READ",
            "SERVICIO_READ", "STATS_READ"
        ));
        roles.get("CONSULTOR_DATOS").setPermissions(allPerms.stream()
            .filter(p -> consultorPerms.contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepository.save(roles.get("CONSULTOR_DATOS"));
        
        // Mantener roles antiguos por compatibilidad
        adminLegacy.setPermissions(new HashSet<>(allPerms));
        roleRepository.save(adminLegacy);
        
        // VETERINARIO (legacy)
        Set<String> vetNames = new HashSet<>(Arrays.asList(
            "USUARIO_READ",
            "CLIENTE_READ", "MASCOTA_CREATE", "MASCOTA_READ",
            "HISTORIAL_CLINICO_CREATE", "HISTORIAL_CLINICO_READ", "HISTORIAL_CLINICO_UPDATE",
            "ARCHIVO_CLINICO_CREATE", "ARCHIVO_CLINICO_READ", "ARCHIVO_CLINICO_UPDATE",
            "CITA_READ", "CITA_SERVICIO_CREATE", "CITA_SERVICIO_READ", "CITA_SERVICIO_UPDATE",
            "SERVICIO_READ", "STATS_READ"
        ));
        vetLegacy.setPermissions(allPerms.stream().filter(p -> vetNames.contains(p.getName())).collect(Collectors.toSet()));
        roleRepository.save(vetLegacy);
        
        // EMPLEADO (legacy)
        Set<String> empNames = new HashSet<>(Arrays.asList(
            "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE",
            "MASCOTA_CREATE", "MASCOTA_READ", "MASCOTA_UPDATE",
            "HISTORIAL_CLINICO_READ",
            "CITA_CREATE", "CITA_READ", "CITA_UPDATE",
            "CITA_SERVICIO_CREATE", "CITA_SERVICIO_READ", "CITA_SERVICIO_UPDATE",
            "SERVICIO_READ"
        ));
        empLegacy.setPermissions(allPerms.stream().filter(p -> empNames.contains(p.getName())).collect(Collectors.toSet()));
        roleRepository.save(empLegacy);
    }
}