package com.backend.vet.ignaciotapia.tdd;

import com.backend.vet.controller.UsuarioController;
import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.dto.UsuarioUpdateDto;
import com.backend.vet.service.UsuarioService;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias TDD para UsuarioController
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para UsuarioController")
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDto usuarioDto;
    private UsuarioUpdateDto usuarioUpdateDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();

        usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setNombreUsuario("veterinario1");
        usuarioDto.setCorreo("vet1@test.com");
        usuarioDto.setRolNombre("VETERINARIO");
        usuarioDto.setActivo(true);

        usuarioUpdateDto = new UsuarioUpdateDto();
        usuarioUpdateDto.setCorreo("vet1_updated@test.com");
        usuarioUpdateDto.setNombreUsuario("veterinario1_updated");
    }

    @Test
    @DisplayName("debería obtener todos los usuarios exitosamente")
    void deberiaObtenerTodosLosUsuariosExitosamente() throws Exception {
        // 1. PREPARACIÓN
        List<UsuarioDto> usuarios = Arrays.asList(usuarioDto, new UsuarioDto());
        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreUsuario", is("veterinario1")));
        
        verify(usuarioService, times(1)).getAllUsuarios();
    }

    @Test
    @DisplayName("debería obtener usuario por ID cuando existe")
    void deberiaObtenerUsuarioPorIdCuandoExiste() throws Exception {
        // 1. PREPARACIÓN
        when(usuarioService.getUsuarioById(1L)).thenReturn(usuarioDto);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(get("/api/usuarios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario", is("veterinario1")))
                .andExpect(jsonPath("$.correo", is("vet1@test.com")));
        
        verify(usuarioService, times(1)).getUsuarioById(1L);
    }

    @Test
    @DisplayName("debería retornar 404 cuando usuario no existe")
    void deberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        // 1. PREPARACIÓN
        when(usuarioService.getUsuarioById(999L)).thenReturn(null);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(get("/api/usuarios/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isNotFound());
        
        verify(usuarioService, times(1)).getUsuarioById(999L);
    }

    @Test
    @DisplayName("debería actualizar usuario exitosamente")
    void deberiaActualizarUsuarioExitosamente() throws Exception {
        // 1. PREPARACIÓN
        UsuarioDto usuarioActualizado = new UsuarioDto();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setCorreo("vet1_updated@test.com");
        usuarioActualizado.setNombreUsuario("veterinario1_updated");
        
        when(usuarioService.updateUsuario(eq(1L), any(UsuarioUpdateDto.class)))
                .thenReturn(usuarioActualizado);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioUpdateDto)))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo", is("vet1_updated@test.com")))
                .andExpect(jsonPath("$.nombreUsuario", is("veterinario1_updated")));
        
        verify(usuarioService, times(1)).updateUsuario(eq(1L), any(UsuarioUpdateDto.class));
    }
}