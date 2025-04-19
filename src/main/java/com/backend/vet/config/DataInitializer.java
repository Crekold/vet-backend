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
    }

    @Override
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

        // 2. Crear o recuperar roles
        Role admin = roleRepository.findByNombre("ADMIN").orElseGet(() -> roleRepository.save(new Role(null, "ADMIN", new HashSet<>())));
        Role vet = roleRepository.findByNombre("VETERINARIO").orElseGet(() -> roleRepository.save(new Role(null, "VETERINARIO", new HashSet<>())));
        Role emp = roleRepository.findByNombre("EMPLEADO").orElseGet(() -> roleRepository.save(new Role(null, "EMPLEADO", new HashSet<>())));

        // 3. Asignar permisos a ADMIN (todos)
        admin.setPermissions(new HashSet<>(allPerms));
        roleRepository.save(admin);

        // 4. Permisos Veterinario
        Set<String> vetNames = new HashSet<>(Arrays.asList(
            "USUARIO_READ",
            "CLIENTE_READ","MASCOTA_CREATE","MASCOTA_READ",
            "HISTORIAL_CLINICO_CREATE","HISTORIAL_CLINICO_READ","HISTORIAL_CLINICO_UPDATE",
            "ARCHIVO_CLINICO_CREATE","ARCHIVO_CLINICO_READ","ARCHIVO_CLINICO_UPDATE",
            "CITA_READ","CITA_SERVICIO_CREATE","CITA_SERVICIO_READ","CITA_SERVICIO_UPDATE",
            "SERVICIO_READ","STATS_READ"
        ));
        vet.setPermissions(allPerms.stream().filter(p -> vetNames.contains(p.getName())).collect(Collectors.toSet()));
        roleRepository.save(vet);

        // 5. Permisos Empleado
        Set<String> empNames = new HashSet<>(Arrays.asList(
            "CLIENTE_CREATE","CLIENTE_READ","CLIENTE_UPDATE",
            "MASCOTA_CREATE","MASCOTA_READ","MASCOTA_UPDATE",
            "HISTORIAL_CLINICO_READ",
            "CITA_CREATE","CITA_READ","CITA_UPDATE",
            "CITA_SERVICIO_CREATE","CITA_SERVICIO_READ","CITA_SERVICIO_UPDATE",
            "SERVICIO_READ"
        ));
        emp.setPermissions(allPerms.stream().filter(p -> empNames.contains(p.getName())).collect(Collectors.toSet()));
        roleRepository.save(emp);
    }
}