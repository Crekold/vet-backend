package com.backend.vet.ezequielgomez;

import com.backend.vet.controller.CitaController;
import com.backend.vet.dto.CitaDto;
import com.backend.vet.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para CitaController
 * Autor: Ezequiel Gomez
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para CitaController")
class CitaControllerTest {

    @Mock
    private CitaService citaService;

    @InjectMocks
    private CitaController citaController;

    private CitaDto citaDto;
    private LocalDate fechaCita;
    private LocalTime horaCita;

    @BeforeEach
    void setUp() {
        // Configuración común para todas las pruebas
        fechaCita = LocalDate.now().plusDays(7);
        horaCita = LocalTime.of(10, 30);

        citaDto = new CitaDto();
        citaDto.setId(1L);
        citaDto.setFecha(fechaCita);
        citaDto.setHora(horaCita);
        citaDto.setMotivo("Consulta veterinaria general");
        citaDto.setEstado("Pendiente");
        citaDto.setMascotaId(1L);
        citaDto.setMascotaNombre("Rex");
        citaDto.setEspecie("Perro");
        citaDto.setRaza("Labrador");
        citaDto.setUsuarioId(1L);
        citaDto.setUsuarioNombre("Dr. García");
        citaDto.setClienteId(1L);
        citaDto.setClienteNombre("Juan Pérez");
    }

    @Test
    @DisplayName("debería obtener todas las citas exitosamente")
    void deberiaObtenerTodasLasCitasExitosamente() {
        // 1. PREPARACIÓN
        CitaDto citaDto2 = new CitaDto();
        citaDto2.setId(2L);
        citaDto2.setFecha(fechaCita.plusDays(1));
        citaDto2.setHora(horaCita.plusHours(2));
        citaDto2.setEstado("Pendiente");
        
        List<CitaDto> citasEsperadas = Arrays.asList(citaDto, citaDto2);
        when(citaService.getAllCitas()).thenReturn(citasEsperadas);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<CitaDto>> response = citaController.getAllCitas();

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Rex", response.getBody().get(0).getMascotaNombre());
        
        verify(citaService, times(1)).getAllCitas();
    }

    @Test
    @DisplayName("debería crear una nueva cita exitosamente con todos los datos requeridos")
    void deberiaCrearNuevaCitaExitosamenteConTodosLosDatosRequeridos() {
        // 1. PREPARACIÓN
        CitaDto nuevaCitaDto = new CitaDto();
        nuevaCitaDto.setFecha(fechaCita);
        nuevaCitaDto.setHora(horaCita);
        nuevaCitaDto.setMotivo("Vacunación anual");
        nuevaCitaDto.setMascotaId(2L);
        nuevaCitaDto.setUsuarioId(1L);

        CitaDto citaCreada = new CitaDto();
        citaCreada.setId(3L);
        citaCreada.setFecha(fechaCita);
        citaCreada.setHora(horaCita);
        citaCreada.setMotivo("Vacunación anual");
        citaCreada.setEstado("Pendiente");
        citaCreada.setMascotaId(2L);
        citaCreada.setUsuarioId(1L);

        when(citaService.createCita(any(CitaDto.class))).thenReturn(citaCreada);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<CitaDto> response = citaController.createCita(nuevaCitaDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Vacunación anual", response.getBody().getMotivo());
        assertEquals("Pendiente", response.getBody().getEstado());
        assertEquals(fechaCita, response.getBody().getFecha());
        assertEquals(horaCita, response.getBody().getHora());
        
        verify(citaService, times(1)).createCita(any(CitaDto.class));
    }

    @Test
    @DisplayName("debería actualizar una cita existente correctamente")
    void deberiaActualizarCitaExistenteCorrectamente() {
        // 1. PREPARACIÓN
        Long citaId = 1L;
        CitaDto citaActualizada = new CitaDto();
        citaActualizada.setId(citaId);
        citaActualizada.setFecha(fechaCita.plusDays(1));
        citaActualizada.setHora(horaCita.plusHours(1));
        citaActualizada.setMotivo("Consulta de seguimiento");
        citaActualizada.setEstado("Atendida");

        when(citaService.updateCita(eq(citaId), any(CitaDto.class)))
                .thenReturn(citaActualizada);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<CitaDto> response = citaController.updateCita(citaId, citaActualizada);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(citaId, response.getBody().getId());
        assertEquals("Consulta de seguimiento", response.getBody().getMotivo());
        assertEquals("Atendida", response.getBody().getEstado());
        assertEquals(fechaCita.plusDays(1), response.getBody().getFecha());
        
        verify(citaService, times(1)).updateCita(eq(citaId), any(CitaDto.class));
    }

    @Test
    @DisplayName("debería eliminar una cita existente y retornar no content")
    void deberiaEliminarCitaExistenteYRetornarNoContent() {
        // 1. PREPARACIÓN
        Long citaId = 1L;
        when(citaService.deleteCita(citaId)).thenReturn(true);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<Void> response = citaController.deleteCita(citaId);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(citaService, times(1)).deleteCita(citaId);
    }

    @Test
    @DisplayName("debería obtener citas por mascota filtrando correctamente")
    void deberiaObtenerCitasPorMascotaFiltrandoCorrectamente() {
        // 1. PREPARACIÓN
        Long mascotaId = 1L;
        
        CitaDto cita1 = new CitaDto();
        cita1.setId(1L);
        cita1.setMascotaId(mascotaId);
        cita1.setMascotaNombre("Rex");
        cita1.setFecha(fechaCita);
        cita1.setHora(horaCita);
        cita1.setEstado("Pendiente");
        
        CitaDto cita2 = new CitaDto();
        cita2.setId(2L);
        cita2.setMascotaId(mascotaId);
        cita2.setMascotaNombre("Rex");
        cita2.setFecha(fechaCita.plusDays(7));
        cita2.setHora(horaCita);
        cita2.setEstado("Atendida");

        List<CitaDto> citasDeMascota = Arrays.asList(cita1, cita2);
        when(citaService.getCitasByMascotaId(mascotaId)).thenReturn(citasDeMascota);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<CitaDto>> response = citaController.getCitasByMascotaId(mascotaId);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        
        // Verificar que todas las citas pertenecen a la misma mascota
        response.getBody().forEach(cita -> {
            assertEquals(mascotaId, cita.getMascotaId());
            assertEquals("Rex", cita.getMascotaNombre());
        });
        
        verify(citaService, times(1)).getCitasByMascotaId(mascotaId);
    }

}
