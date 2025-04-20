package com.backend.vet.service;

import com.backend.vet.dto.CitaServicioDto;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Cita;
import com.backend.vet.model.CitaServicio;
import com.backend.vet.model.CitaServicioId;
import com.backend.vet.model.Servicio;
import com.backend.vet.repository.CitaRepository;
import com.backend.vet.repository.CitaServicioRepository;
import com.backend.vet.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaServicioService {
    
    @Autowired
    private CitaServicioRepository citaServicioRepository;
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    public List<CitaServicioDto> getServiciosByCita(Long citaId) {
        return citaServicioRepository.findByCitaId(citaId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CitaServicioDto> getCitasByServicio(Long servicioId) {
        return citaServicioRepository.findByServicioId(servicioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CitaServicioDto addServicioToCita(CitaServicioDto citaServicioDto) {
        Cita cita = citaRepository.findById(citaServicioDto.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaServicioDto.getCitaId()));
        
        Servicio servicio = servicioRepository.findById(citaServicioDto.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", citaServicioDto.getServicioId()));
        
        CitaServicioId id = new CitaServicioId(citaServicioDto.getCitaId(), citaServicioDto.getServicioId());
        
        // Verificar si ya existe la relación
        if (citaServicioRepository.existsById(id)) {
             throw new IllegalStateException("El servicio ya está asociado a esta cita.");
        }

        CitaServicio citaServicio = new CitaServicio();
        citaServicio.setId(id);
        citaServicio.setCita(cita); // Asegúrate de que la referencia a Cita está establecida
        citaServicio.setServicio(servicio); // Asegúrate de que la referencia a Servicio está establecida
        citaServicio.setCantidad(citaServicioDto.getCantidad());
        
        // Ya no añadimos explícitamente a la colección de Cita
        // cita.getServicios().add(citaServicio); // Línea eliminada/comentada
        
        // Guardar la entidad CitaServicio. Hibernate/JPA debería manejar la relación.
        CitaServicio savedCitaServicio = citaServicioRepository.save(citaServicio);
        
        // No es necesario guardar 'cita' explícitamente aquí a menos que 
        // se hayan hecho otros cambios en 'cita' que necesiten ser persistidos.
        // citaRepository.save(cita); 

        return convertToDto(savedCitaServicio);
    }
    
    @Transactional
    public CitaServicioDto updateCitaServicio(CitaServicioDto citaServicioDto) {
        CitaServicioId id = new CitaServicioId(citaServicioDto.getCitaId(), citaServicioDto.getServicioId());
        
        CitaServicio citaServicio = citaServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CitaServicio", "id", id.toString()));
        
        citaServicio.setCantidad(citaServicioDto.getCantidad());
        
        CitaServicio updatedCitaServicio = citaServicioRepository.save(citaServicio);
        return convertToDto(updatedCitaServicio);
    }
    
    @Transactional
    public boolean removeServicioFromCita(Long citaId, Long servicioId) {
        CitaServicioId id = new CitaServicioId(citaId, servicioId);
        
        if (!citaServicioRepository.existsById(id)) {
            return false;
        }
        
        try {
            citaServicioRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Transactional
    public boolean removeAllServiciosFromCita(Long citaId) {
        if (!citaRepository.existsById(citaId)) {
            return false;
        }
        
        try {
            citaServicioRepository.deleteByCitaId(citaId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private CitaServicioDto convertToDto(CitaServicio citaServicio) {
        CitaServicioDto dto = new CitaServicioDto();
        dto.setCitaId(citaServicio.getId().getCitaId());
        dto.setServicioId(citaServicio.getId().getServicioId());
        dto.setCantidad(citaServicio.getCantidad());
        
        if (citaServicio.getServicio() != null) {
            dto.setNombreServicio(citaServicio.getServicio().getNombre());
            dto.setDescripcionServicio(citaServicio.getServicio().getDescripcion());
            dto.setPrecioUnitario(citaServicio.getServicio().getPrecio());
            
            // Calcular precio total
            if (citaServicio.getServicio().getPrecio() != null) {
                dto.setPrecioTotal(citaServicio.getServicio().getPrecio()
                        .multiply(java.math.BigDecimal.valueOf(citaServicio.getCantidad())));
            }
        }
        
        return dto;
    }
}
