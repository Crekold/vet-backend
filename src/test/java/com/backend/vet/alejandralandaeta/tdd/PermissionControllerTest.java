package com.backend.vet.alejandralandaeta.tdd;

import com.backend.vet.controller.PermissionController;
import com.backend.vet.dto.PermissionDto;
import com.backend.vet.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para PermissionController
 * Autor: Alejandra Landaeta
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para PermissionController")
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private List<PermissionDto> permisosCompletos;

    @BeforeEach
    void setUp() {
        // Configuración de permisos del sistema
        permisosCompletos = Arrays.asList(
                new PermissionDto(1L, "HISTORIAL_CLINICO_READ"),
                new PermissionDto(2L, "HISTORIAL_CLINICO_CREATE"),
                new PermissionDto(3L, "HISTORIAL_CLINICO_UPDATE"),
                new PermissionDto(4L, "HISTORIAL_CLINICO_DELETE"),
                new PermissionDto(5L, "SERVICIO_READ"),
                new PermissionDto(6L, "SERVICIO_CREATE"),
                new PermissionDto(7L, "SERVICIO_UPDATE"),
                new PermissionDto(8L, "SERVICIO_DELETE"),
                new PermissionDto(9L, "PERMISSION_READ"),
                new PermissionDto(10L, "CITA_READ"),
                new PermissionDto(11L, "CITA_CREATE"),
                new PermissionDto(12L, "CITA_UPDATE"),
                new PermissionDto(13L, "CITA_DELETE")
        );
    }


    @Test
    @DisplayName("debería agrupar permisos por módulo y contar correctamente")
    void deberiaAgruparPermisosYContar() {
        // 1. PREPARACIÓN
        when(permissionService.getAllPermissions()).thenReturn(permisosCompletos);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<PermissionDto>> response = permissionController.getAllPermissions();

        // 3. VERIFICACIÓN CON ASSERT - Agrupación y conteo
        long permisosCita = response.getBody().stream()
                .filter(p -> p.getName().startsWith("CITA"))
                .count();
        
        long permisosServicio = response.getBody().stream()
                .filter(p -> p.getName().startsWith("SERVICIO"))
                .count();
        
        long permisosHistorial = response.getBody().stream()
                .filter(p -> p.getName().startsWith("HISTORIAL"))
                .count();

        assertEquals(4, permisosCita);
        assertEquals(4, permisosServicio);
        assertEquals(4, permisosHistorial);
    }

 
    @Test
    @DisplayName("debería validar que cada permiso tiene ID único no nulo")
    void deberiaValidarIntegridadDeIds() {
        // 1. PREPARACIÓN
        when(permissionService.getAllPermissions()).thenReturn(permisosCompletos);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<PermissionDto>> response = permissionController.getAllPermissions();

        // 3. VERIFICACIÓN CON ASSERT - Validación rebuscada de IDs únicos
        List<Long> ids = response.getBody().stream()
                .map(PermissionDto::getId)
                .collect(Collectors.toList());

        // Validación: no nulos
        ids.forEach(id -> assertNotNull(id));

        // Validación: todos únicos
        long uniqueIds = ids.stream().distinct().count();
        assertEquals(ids.size(), uniqueIds);

        // Validación: en rango positivo
        ids.forEach(id -> assertTrue(id > 0));
    }

    @Test
    @DisplayName("debería validar nombres de permisos sin valores nulos o duplicados")
    void deberiaValidarNombresDePermisosUnicos() {
        // 1. PREPARACIÓN
        when(permissionService.getAllPermissions()).thenReturn(permisosCompletos);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<PermissionDto>> response = permissionController.getAllPermissions();

        // 3. VERIFICACIÓN CON ASSERT
        List<String> nombres = response.getBody().stream()
                .map(PermissionDto::getName)
                .collect(Collectors.toList());

        // Sin nulos
        nombres.forEach(name -> assertNotNull(name));
        nombres.forEach(name -> assertFalse(name.isEmpty()));

        // Sin duplicados
        long nombresUnicos = nombres.stream().distinct().count();
        assertEquals(nombres.size(), nombresUnicos);

        // Formato consistente (MAYÚSCULAS_CON_GUIONES)
        nombres.forEach(name -> {
            assertTrue(name.matches("^[A-Z_]+$"), 
                    "El permiso " + name + " no cumple formato MAYÚSCULAS_GUIONES");
        });
    }
}
