package com.backend.vet.fabriziopalenque.tdd;

import com.backend.vet.controller.ClienteController;
import com.backend.vet.dto.ClienteDto;
import com.backend.vet.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para ClienteController")
public class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("debería listar todos los clientes")
    void deberiaListarTodosLosClientes() throws Exception {
        // 1. PREPARACIÓN
        ClienteDto cliente1 = new ClienteDto(1L, "Fabrizio", "Palenque", "12345678", "fabri@test.com", "Av. Siempre Viva 123", LocalDateTime.now());
        ClienteDto cliente2 = new ClienteDto(2L, "Ezequiel", "Gomez", "87654321", "eze@test.com", "Av. Falsa 456", LocalDateTime.now());
        List<ClienteDto> clientes = Arrays.asList(cliente1, cliente2);
        when(clienteService.getAllClientes()).thenReturn(clientes);

        // 2. LÓGICA DE LA PRUEBA
        var response = clienteController.getAllClientes();

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Fabrizio", response.getBody().get(0).getNombre());
        assertEquals("Ezequiel", response.getBody().get(1).getNombre());
    }

    @Test
    @DisplayName("debería obtener un cliente por su ID")
    void deberiaObtenerClientePorId() throws Exception {
        // 1. PREPARACIÓN
        ClienteDto cliente = new ClienteDto(1L, "Fabrizio", "Palenque", "12345678", "fabri@test.com", "Av. Siempre Viva 123", LocalDateTime.now());
        when(clienteService.getClienteById(1L)).thenReturn(cliente);

        // 2. LÓGICA DE LA PRUEBA
        var response = clienteController.getClienteById(1L);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Fabrizio", response.getBody().getNombre());
    }

    @Test
    @DisplayName("debería devolver 404 cuando el cliente no existe")
    void deberiaDevolver404CuandoClienteNoExiste() throws Exception {
        // 1. PREPARACIÓN
        when(clienteService.getClienteById(99L)).thenReturn(null);

        // 2. LÓGICA DE LA PRUEBA
        var response = clienteController.getClienteById(99L);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("debería crear un nuevo cliente")
    void deberiaCrearNuevoCliente() throws Exception {
        // 1. PREPARACIÓN
        ClienteDto clienteDto = new ClienteDto(null, "Nuevo", "Cliente", "11223344", "nuevo@test.com", "Calle Nueva 789", null);
        ClienteDto clienteGuardado = new ClienteDto(1L, "Nuevo", "Cliente", "11223344", "nuevo@test.com", "Calle Nueva 789", LocalDateTime.now());
        when(clienteService.createCliente(any(ClienteDto.class))).thenReturn(clienteGuardado);

        // 2. LÓGICA DE LA PRUEBA - Usar MockMvc para simular el contexto HTTP
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDto)))
                // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("debería actualizar un cliente existente")
    void deberiaActualizarClienteExistente() {
        // PREPARACIÓN
        ClienteDto clienteDto = new ClienteDto(1L, "Fabrizio", "Actualizado", "12345678", "fabri.upd@test.com", "Av. Siempre Viva 123", LocalDateTime.now());
        when(clienteService.updateCliente(anyLong(), any(ClienteDto.class))).thenReturn(clienteDto);
    
        // LÓGICA DE LA PRUEBA
        var response = clienteController.updateCliente(1L, clienteDto);
    
        // VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Actualizado", response.getBody().getApellido());
        assertEquals("fabri.upd@test.com", response.getBody().getCorreo());
    }
}