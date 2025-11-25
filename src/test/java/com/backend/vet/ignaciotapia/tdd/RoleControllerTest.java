package com.backend.vet.ignaciotapia.tdd;

import com.backend.vet.controller.RoleController;
import com.backend.vet.dto.PermissionDto;
import com.backend.vet.dto.RoleDto;
import com.backend.vet.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias TDD para RoleController
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para RoleController")
class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private ObjectMapper objectMapper;
    private RoleDto roleDto;
    private PermissionDto permissionDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
        objectMapper = new ObjectMapper();

        roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setNombre("VETERINARIO");
        // roleDto.setDescripcion("Rol de veterinario"); // Campo no existe en DTO

        permissionDto = new PermissionDto();
        permissionDto.setId(1L);
        permissionDto.setName("USUARIO_READ");
    }

    @Test
    @DisplayName("debería crear rol exitosamente")
    void deberiaCrearRolExitosamente() throws Exception {
        // 1. PREPARACIÓN
        when(roleService.createRole(any(RoleDto.class))).thenReturn(roleDto);

        // 2. LÓGICA DE LA PRUEBA
        var resultActions = mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        // 3. VERIFICACIÓN CON ASSERT
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("VETERINARIO"));
        
        verify(roleService, times(1)).createRole(any(RoleDto.class));
    }

    @Test
    @DisplayName("debería obtener permisos de un rol exitosamente")
    void deberiaObtenerPermisosDeRolExitosamente() throws Exception {
        // 1. PREPARACIÓN
        List<PermissionDto> permisos = Arrays.asList(permissionDto);
        when(roleService.getPermissionsByRole(1L)).thenReturn(permisos);

        // 2. LÓGICA DE LA PRUEBA
        var resultActions = mockMvc.perform(get("/api/roles/1/permissions"));

        // 3. VERIFICACIÓN CON ASSERT
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("USUARIO_READ"));

        verify(roleService, times(1)).getPermissionsByRole(1L);
    }

    @Test
    @DisplayName("debería actualizar permisos de rol exitosamente")
    void deberiaActualizarPermisosDeRolExitosamente() throws Exception {
        // 1. PREPARACIÓN
        Set<Long> permissionIds = new HashSet<>(Arrays.asList(1L, 2L));
        List<PermissionDto> permisosActualizados = Arrays.asList(permissionDto, new PermissionDto());
        
        when(roleService.updatePermissions(eq(1L), any(Set.class)))
                .thenReturn(permisosActualizados);

        // 2. LÓGICA DE LA PRUEBA
        var resultActions = mockMvc.perform(put("/api/roles/1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionIds)));

        // 3. VERIFICACIÓN CON ASSERT
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(roleService, times(1)).updatePermissions(eq(1L), any(Set.class));
    }
}