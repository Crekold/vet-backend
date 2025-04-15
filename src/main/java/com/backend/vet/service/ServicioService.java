package com.backend.vet.service;

import com.backend.vet.dto.ServicioDto;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Servicio;
import com.backend.vet.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    public List<ServicioDto> getAllServicios() {
        return servicioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ServicioDto getServicioById(Long id) {
        return servicioRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public List<ServicioDto> getServiciosByNombre(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ServicioDto> getServiciosPrecioMenorIgual(BigDecimal precio) {
        return servicioRepository.findByPrecioLessThanEqual(precio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ServicioDto> getServiciosPrecioMayorIgual(BigDecimal precio) {
        return servicioRepository.findByPrecioGreaterThanEqual(precio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los servicios veterinarios excluyendo peluquería
     * @return lista de servicios veterinarios
     */
    public List<ServicioDto> findAllExcluyendoPeluqueria() {
        return servicioRepository.findByNombreNot("Peluquería").stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ServicioDto createServicio(ServicioDto servicioDto) {
        Servicio servicio = new Servicio();
        servicio.setNombre(servicioDto.getNombre());
        servicio.setDescripcion(servicioDto.getDescripcion());
        servicio.setPrecio(servicioDto.getPrecio());
        
        Servicio savedServicio = servicioRepository.save(servicio);
        return convertToDto(savedServicio);
    }
    
    @Transactional
    public ServicioDto updateServicio(Long id, ServicioDto servicioDto) {
        try {
            Servicio servicio = servicioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", id));
            
            servicio.setNombre(servicioDto.getNombre());
            servicio.setDescripcion(servicioDto.getDescripcion());
            servicio.setPrecio(servicioDto.getPrecio());
            
            Servicio updatedServicio = servicioRepository.save(servicio);
            return convertToDto(updatedServicio);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteServicio(Long id) {
        if (!servicioRepository.existsById(id)) {
            return false;
        }
        servicioRepository.deleteById(id);
        return true;
    }
    
    private ServicioDto convertToDto(Servicio servicio) {
        ServicioDto dto = new ServicioDto();
        dto.setId(servicio.getId());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setPrecio(servicio.getPrecio());
        return dto;
    }
}
