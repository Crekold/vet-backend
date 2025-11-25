package com.backend.vet.alejandralandaeta.tdd;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para ServicioController
 * Autor: Alejandra Landaeta
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para ServicioController")
class ServicioControllerTest {

    @Mock
    private ServicioService servicioService;

    @InjectMocks
    private ServicioController servicioController;

    private List<ServicioDto> serviciosCompletos;

    @BeforeEach
    void setUp() {
        serviciosCompletos = Arrays.asList(
                crearServicio(1L, "Consulta General", "Revisión básica", new BigDecimal("50.00")),
                crearServicio(2L, "Vacunación", "Aplicación de vacunas", new BigDecimal("35.00")),
                crearServicio(3L, "Radiografía", "Estudio radiológico", new BigDecimal("150.00")),
                crearServicio(4L, "Cirugía Menor", "Procedimientos quirúrgicos menores", new BigDecimal("300.00")),
                crearServicio(5L, "Peluquería", "Baño y corte de pelo", new BigDecimal("60.00")),
                crearServicio(6L, "Ultrasonido", "Ecografía abdominal", new BigDecimal("120.00")),
                crearServicio(7L, "Análisis de Sangre", "Laboratorio completo", new BigDecimal("80.00")),
                crearServicio(8L, "Limpieza Dental", "Profilaxis dental", new BigDecimal("90.00"))
        );
    }

    private ServicioDto crearServicio(Long id, String nombre, String descripcion, BigDecimal precio) {
        ServicioDto dto = new ServicioDto();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setDescripcion(descripcion);
        dto.setPrecio(precio);
        return dto;
    }

    @Test
    @DisplayName("debería buscar servicios por nombre con coincidencias parciales case-insensitive")
    void deberiaFiltrarPorNombreConCoincidenciasParciales() {
        // 1. PREPARACIÓN
        String busqueda = "aná";
        
        List<ServicioDto> coincidencias = serviciosCompletos.stream()
                .filter(s -> s.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                .collect(Collectors.toList());

        when(servicioService.getServiciosByNombre(busqueda))
                .thenReturn(coincidencias);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<ServicioDto>> response = 
                servicioController.getServiciosByNombre(busqueda);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0);

        // Validación: todos contienen la búsqueda (case-insensitive)
        response.getBody().forEach(servicio -> {
            assertTrue(servicio.getNombre().toLowerCase().contains(busqueda.toLowerCase()),
                    "El servicio " + servicio.getNombre() + " no contiene la búsqueda");
        });

        verify(servicioService, times(1)).getServiciosByNombre(busqueda);
    }

    @Test
    @DisplayName("debería retornar lista vacía cuando no hay servicios en el rango de precio")
    void deberiaRetornarVacioSiNoHayServiciosEnRango() {
        // 1. PREPARACIÓN
        BigDecimal precioMuybajo = new BigDecimal("10.00");
        when(servicioService.getServiciosPrecioMenorIgual(precioMuybajo))
                .thenReturn(Collections.emptyList());

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<List<ServicioDto>> response = 
                servicioController.getServiciosPrecioMenorIgual(precioMuybajo);

        // 3. VERIFICACIÓN CON ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(servicioService, times(1)).getServiciosPrecioMenorIgual(precioMuybajo);
    }

}