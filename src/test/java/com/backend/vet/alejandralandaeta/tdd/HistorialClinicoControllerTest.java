package com.backend.vet.alejandralandaeta.tdd;

import com.backend.vet.controller.HistorialClinicoController;
import com.backend.vet.dto.HistorialClinicoDto;
import com.backend.vet.service.HistorialClinicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para HistorialClinicoController
 * Autor: Alejandra Landaeta
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para HistorialClinicoController")
class HistorialClinicoControllerTest {

    @Mock
    private HistorialClinicoService historialClinicoService;

    @InjectMocks
    private HistorialClinicoController historialClinicoController;

    private HistorialClinicoDto historialDto;
    private LocalDateTime fechaActual;

    @BeforeEach
    void setUp() {
        fechaActual = LocalDateTime.now();

        historialDto = new HistorialClinicoDto();
        historialDto.setId(1L);
        historialDto.setFecha(fechaActual);
        historialDto.setDiagnostico("Otitis media - Infección en oído");
        historialDto.setTratamiento("Antibiótico + Analgésico");
        historialDto.setObservaciones("Paciente respondió bien al tratamiento");
        historialDto.setMascotaId(1L);
        historialDto.setMascotaNombre("Rex");
        historialDto.setUsuarioId(1L);
        historialDto.setUsuarioNombre("Dr. García");
        historialDto.setClienteId(1L);
        historialDto.setClienteNombre("Juan Pérez");
        historialDto.setCitaId(1L);
    }

    @Test
    @DisplayName("debería obtener historial por cliente filtrando múltiples mascotas del mismo propietario")
    void deberiaObtenerHistorialPorClienteConMultiplesMascotas() {
        // 1. PREPARACIÓN
        Long clienteId = 1L;

        HistorialClinicoDto historial1 = new HistorialClinicoDto();
        historial1.setId(1L);
        historial1.setMascotaId(1L);
        historial1.setMascotaNombre("Rex");
        historial1.setClienteId(clienteId);
        historial1.setClienteNombre("Juan Pérez");
        historial1.setDiagnostico("Otitis");

        HistorialClinicoDto historial2 = new HistorialClinicoDto();
        historial2.setId(2L);
        historial2.setMascotaId(3L);
        historial2.setMascotaNombre("Luna");
        historial2.setClienteId(clienteId);
        historial2.setClienteNombre("Juan Pérez");
        historial2.setDiagnostico("Gastritis");

        HistorialClinicoDto historial3 = new HistorialClinicoDto();
        historial3.setId(3L);
        historial3.setMascotaId(5L);
        historial3.setMascotaNombre("Milo");
        historial3.setClienteId(clienteId);
        historial3.setClienteNombre("Juan Pérez");
        historial3.setDiagnostico("Vacunación");

        List<HistorialClinicoDto> historialesDelCliente = Arrays.asList(historial1, historial2, historial3);
        when(historialClinicoService.getHistorialClinicoByClienteId(clienteId))
                .thenReturn(historialesDelCliente);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<HistorialClinicoDto>> response = 
                historialClinicoController.getHistorialClinicoByClienteId(clienteId);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        
        // Verificación compleja: todas las mascotas pertenecen al mismo cliente
        response.getBody().forEach(h -> {
            assertEquals(clienteId, h.getClienteId());
            assertEquals("Juan Pérez", h.getClienteNombre());
            assertNotNull(h.getMascotaId());
            assertNotNull(h.getMascotaNombre());
        });
        
        // Verificación de mascota IDs únicos
        long uniqueMascotas = response.getBody().stream()
                .map(HistorialClinicoDto::getMascotaId)
                .distinct()
                .count();
        assertEquals(3, uniqueMascotas);
        
        verify(historialClinicoService, times(1)).getHistorialClinicoByClienteId(clienteId);
    }

    @Test
    @DisplayName("debería obtener historial vacío cuando no hay registros para una mascota")
    void deberiaObtenerListaVaciaParaMascotaSinHistorial() {
        // 1. PREPARACIÓN
        Long mascotaSinHistorial = 999L;
        when(historialClinicoService.getHistorialClinicoByMascotaId(mascotaSinHistorial))
                .thenReturn(Collections.emptyList());

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<HistorialClinicoDto>> response = 
                historialClinicoController.getHistorialClinicoByMascotaId(mascotaSinHistorial);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertEquals(0, response.getBody().size());
        
        verify(historialClinicoService, times(1)).getHistorialClinicoByMascotaId(mascotaSinHistorial);
    }

    @Test
    @DisplayName("debería crear historial con relación a cita y validar integridad referencial")
    void deberiaCrearHistorialConRelacionACitaYValidarIntegridad() {
        // 1. PREPARACIÓN
        HistorialClinicoDto nuevoHistorial = new HistorialClinicoDto();
        nuevoHistorial.setFecha(fechaActual);
        nuevoHistorial.setDiagnostico("Consulta post-operatoria");
        nuevoHistorial.setTratamiento("Reposo - Sin actividad física");
        nuevoHistorial.setMascotaId(1L);
        nuevoHistorial.setUsuarioId(1L);
        nuevoHistorial.setCitaId(5L); // Relación con cita

        HistorialClinicoDto historialCreado = new HistorialClinicoDto();
        historialCreado.setId(10L);
        historialCreado.setFecha(fechaActual);
        historialCreado.setDiagnostico("Consulta post-operatoria");
        historialCreado.setTratamiento("Reposo - Sin actividad física");
        historialCreado.setMascotaId(1L);
        historialCreado.setUsuarioId(1L);
        historialCreado.setCitaId(5L);
        historialCreado.setMascotaNombre("Rex");
        historialCreado.setClienteNombre("Juan Pérez");

        when(historialClinicoService.createHistorialClinico(any(HistorialClinicoDto.class)))
                .thenReturn(historialCreado);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<HistorialClinicoDto> response = 
                historialClinicoController.createHistorialClinico(nuevoHistorial);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        assertEquals(5L, response.getBody().getCitaId());
        assertEquals(1L, response.getBody().getMascotaId());
        assertEquals(1L, response.getBody().getUsuarioId());
        assertNotNull(response.getBody().getMascotaNombre());
        
        verify(historialClinicoService, times(1)).createHistorialClinico(any(HistorialClinicoDto.class));
    }

    @Test
    @DisplayName("debería actualizar historial manteniendo integridad de relaciones múltiples")
    void deberiaActualizarHistorialManteniendoRelacionesMultiples() {
        // 1. PREPARACIÓN
        Long historialId = 1L;
        HistorialClinicoDto actualizacion = new HistorialClinicoDto();
        actualizacion.setId(historialId);
        actualizacion.setFecha(fechaActual.plusHours(2));
        actualizacion.setDiagnostico("Otitis media - Mejorado");
        actualizacion.setTratamiento("Continuar con antibióticos");
        actualizacion.setObservaciones("Respuesta favorable al tratamiento - Mejoria visible");
        actualizacion.setMascotaId(1L);
        actualizacion.setUsuarioId(1L);
        actualizacion.setCitaId(1L);

        HistorialClinicoDto historialActualizado = new HistorialClinicoDto();
        historialActualizado.setId(historialId);
        historialActualizado.setFecha(fechaActual.plusHours(2));
        historialActualizado.setDiagnostico("Otitis media - Mejorado");
        historialActualizado.setTratamiento("Continuar con antibióticos");
        historialActualizado.setObservaciones("Respuesta favorable al tratamiento - Mejoría visible");
        historialActualizado.setMascotaId(1L);
        historialActualizado.setUsuarioId(1L);
        historialActualizado.setCitaId(1L);
        historialActualizado.setMascotaNombre("Rex");
        historialActualizado.setClienteNombre("Juan Pérez");

        when(historialClinicoService.updateHistorialClinico(eq(historialId), any(HistorialClinicoDto.class)))
                .thenReturn(historialActualizado);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<HistorialClinicoDto> response = 
                historialClinicoController.updateHistorialClinico(historialId, actualizacion);

        // 3. VERIFICACIÓN CON ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(historialId, response.getBody().getId());
        assertEquals("Otitis media - Mejorado", response.getBody().getDiagnostico());
        assertEquals("Continuar con antibióticos", response.getBody().getTratamiento());
        assertEquals(1L, response.getBody().getMascotaId());
        assertEquals(1L, response.getBody().getUsuarioId());
        assertEquals(1L, response.getBody().getCitaId());
        
        verify(historialClinicoService, times(1))
                .updateHistorialClinico(eq(historialId), any(HistorialClinicoDto.class));
    }

    @Test
    @DisplayName("debería eliminar historial y retornar no content")
    void deberiaEliminarHistorialYRetornarNoContent() {
        // 1. PREPARACIÓN
        Long historialId = 1L;
        when(historialClinicoService.deleteHistorialClinico(historialId)).thenReturn(true);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<Void> response = historialClinicoController.deleteHistorialClinico(historialId);

        // 3. VERIFICACIÓN CON ASSERT
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(historialClinicoService, times(1)).deleteHistorialClinico(historialId);
    }

    @Test
    void placeholder_noop() {
        assertTrue(true);
    }
}