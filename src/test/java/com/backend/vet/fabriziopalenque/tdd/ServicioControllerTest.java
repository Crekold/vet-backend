package com.backend.vet.fabriziopalenque.tdd;

import com.backend.vet.controller.ServicioController;
import com.backend.vet.dto.ServicioDto;
import com.backend.vet.service.ServicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para ServicioController")
public class ServicioControllerTest {

    @Mock
    private ServicioService servicioService;

    @InjectMocks
    private ServicioController servicioController;

    @Test
    @DisplayName("debería listar todos los servicios (usa BigDecimal en precio)")
    void deberiaListarTodosLosServicios() {
        ServicioDto s1 = new ServicioDto(1L, "Corte", "Corte de pelo", new BigDecimal("12.50"));
        ServicioDto s2 = new ServicioDto(2L, "Baño", "Baño completo", new BigDecimal("8.75"));
        List<ServicioDto> servicios = Arrays.asList(s1, s2);
        when(servicioService.getAllServicios()).thenReturn(servicios);

        var response = servicioController.getAllServicios();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Corte", response.getBody().get(0).getNombre());
        assertEquals("Baño", response.getBody().get(1).getNombre());
    }

    @Test
    @DisplayName("debería crear un nuevo servicio")
    void deberiaCrearNuevoServicio() {
        ServicioDto nuevo = new ServicioDto(null, "Desparasitación", "Desparasitación interna y externa", new BigDecimal("20.00"));
        ServicioDto guardado = new ServicioDto(5L, "Desparasitación", "Desparasitación interna y externa", new BigDecimal("20.00"));
        when(servicioService.createServicio(any(ServicioDto.class))).thenReturn(guardado);

        var response = servicioController.createServicio(nuevo);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getId());
        assertEquals("Desparasitación", response.getBody().getNombre());
    }
}