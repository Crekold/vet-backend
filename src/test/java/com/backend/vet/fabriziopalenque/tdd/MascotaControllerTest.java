package com.backend.vet.fabriziopalenque.tdd;

import com.backend.vet.controller.MascotaController;
import com.backend.vet.dto.MascotaDto;
import com.backend.vet.service.MascotaService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para MascotaController")
public class MascotaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MascotaService mascotaService;

    @InjectMocks
    private MascotaController mascotaController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mascotaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("debería listar todas las mascotas")
    void deberiaListarTodasLasMascotas() {
        // 1. PREPARACIÓN
        MascotaDto mascota1 = new MascotaDto(1L, "Firulais", "Perro", "Labrador", LocalDate.of(2018,1,1), "Cafe", 1L, "Sin alergias", "M");
        MascotaDto mascota2 = new MascotaDto(2L, "Mishi", "Gato", "Siames", LocalDate.of(2020,6,15), "Blanco", 2L, "Vacunada", "H");
        List<MascotaDto> mascotas = Arrays.asList(mascota1, mascota2);
        when(mascotaService.getAllMascotas()).thenReturn(mascotas);

        // 2. LÓGICA DE LA PRUEBA
        var response = mascotaController.getAllMascotas();

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Firulais", response.getBody().get(0).getNombre());
        assertEquals("Mishi", response.getBody().get(1).getNombre());
    }

    @Test
    @DisplayName("debería obtener una mascota por su ID")
    void deberiaObtenerMascotaPorId() {
        // 1. PREPARACIÓN
        MascotaDto mascota = new MascotaDto(1L, "Firulais", "Perro", "Labrador", LocalDate.of(2018,1,1), "Cafe", 1L, "Sin alergias", "M");
        when(mascotaService.getMascotaById(1L)).thenReturn(mascota);

        // 2. LÓGICA DE LA PRUEBA
        var response = mascotaController.getMascotaById(1L);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Firulais", response.getBody().getNombre());
    }

    @Test
    @DisplayName("debería crear una nueva mascota")
    void deberiaCrearNuevaMascota() {
        // 1. PREPARACIÓN
        MascotaDto mascotaDto = new MascotaDto(null, "Rocky", "Perro", "Bulldog", LocalDate.of(2021,3,3), "Negro", 1L, "Activa", "M");
        MascotaDto mascotaGuardada = new MascotaDto(3L, "Rocky", "Perro", "Bulldog", LocalDate.of(2021,3,3), "Negro", 1L, "Activa", "M");
        when(mascotaService.createMascota(any(MascotaDto.class))).thenReturn(mascotaGuardada);

        // 2. LÓGICA DE LA PRUEBA
        var response = mascotaController.createMascota(mascotaDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Rocky", response.getBody().getNombre());
    }
}