package com.backend.vet.ignaciotapia.tdd;

import com.backend.vet.controller.StatsController;
import com.backend.vet.service.CitaService;
import com.backend.vet.service.HistorialClinicoService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias TDD para StatsController
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para StatsController")
class StatsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HistorialClinicoService historialClinicoService;

    @Mock
    private CitaService citaService;

    @InjectMocks
    private StatsController statsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statsController).build();
    }

    @Test
    @DisplayName("debería obtener estadísticas del dashboard exitosamente")
    void deberiaObtenerEstadisticasDashboardExitosamente() throws Exception {
        // 1. PREPARACIÓN
        when(historialClinicoService.countPacientesAtendidos()).thenReturn(25);
        when(citaService.countCitasDelDia()).thenReturn(8);
        when(historialClinicoService.countVacunasAplicadasHoy()).thenReturn(12);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(get("/api/stats/dashboard")
                .contentType(MediaType.APPLICATION_JSON))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pacientesAtendidos").value(25))
                .andExpect(jsonPath("$.citasDelDia").value(8))
                .andExpect(jsonPath("$.vacunasAplicadas").value(12));
        
        verify(historialClinicoService, times(1)).countPacientesAtendidos();
        verify(citaService, times(1)).countCitasDelDia();
        verify(historialClinicoService, times(1)).countVacunasAplicadasHoy();
    }

    @Test
    @DisplayName("debería retornar estadísticas con valores en cero cuando no hay datos")
    void deberiaRetornarEstadisticasConValoresEnCeroCuandoNoHayDatos() throws Exception {
        // 1. PREPARACIÓN
        when(historialClinicoService.countPacientesAtendidos()).thenReturn(0);
        when(citaService.countCitasDelDia()).thenReturn(0);
        when(historialClinicoService.countVacunasAplicadasHoy()).thenReturn(0);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(get("/api/stats/dashboard")
                .contentType(MediaType.APPLICATION_JSON))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pacientesAtendidos").value(0))
                .andExpect(jsonPath("$.citasDelDia").value(0))
                .andExpect(jsonPath("$.vacunasAplicadas").value(0));
        
        verify(historialClinicoService, times(1)).countPacientesAtendidos();
        verify(citaService, times(1)).countCitasDelDia();
        verify(historialClinicoService, times(1)).countVacunasAplicadasHoy();
    }

    @Test
    @DisplayName("debería retornar todas las claves esperadas en el mapa de estadísticas")
    void deberiaRetornarTodasLasClavesEsperadasEnMapaEstadisticas() throws Exception {
        // 1. PREPARACIÓN
        when(historialClinicoService.countPacientesAtendidos()).thenReturn(10);
        when(citaService.countCitasDelDia()).thenReturn(5);
        when(historialClinicoService.countVacunasAplicadasHoy()).thenReturn(3);

        // 2. LÓGICA DE LA PRUEBA
        mockMvc.perform(get("/api/stats/dashboard")
                .contentType(MediaType.APPLICATION_JSON))

        // 3. VERIFICACIÓN CON ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pacientesAtendidos").exists())
                .andExpect(jsonPath("$.citasDelDia").exists())
                .andExpect(jsonPath("$.vacunasAplicadas").exists());
    }
}