package com.backend.vet.service;

import com.backend.vet.dto.HistorialClinicoDto;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Cita;
import com.backend.vet.model.HistorialClinico;
import com.backend.vet.model.Mascota;
import com.backend.vet.model.Usuario;
import com.backend.vet.repository.CitaRepository;
import com.backend.vet.repository.HistorialClinicoRepository;
import com.backend.vet.repository.MascotaRepository;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialClinicoService {
    
    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CitaRepository citaRepository;
    
    public List<HistorialClinicoDto> getAllHistorialClinico() {
        return historialClinicoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public HistorialClinicoDto getHistorialClinicoById(Long id) {
        return historialClinicoRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public List<HistorialClinicoDto> getHistorialClinicoByMascotaId(Long mascotaId) {
        return historialClinicoRepository.findByMascotaId(mascotaId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<HistorialClinicoDto> getHistorialClinicoByClienteId(Long clienteId) {
        return historialClinicoRepository.findByMascotaClienteId(clienteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<HistorialClinicoDto> getHistorialClinicoByVeterinarioId(Long veterinarioId) {
        return historialClinicoRepository.findByUsuarioId(veterinarioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<HistorialClinicoDto> getHistorialClinicoByCitaId(Long citaId) {
        return historialClinicoRepository.findByCitaId(citaId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<HistorialClinicoDto> getHistorialClinicoByFechaRango(LocalDateTime inicio, LocalDateTime fin) {
        return historialClinicoRepository.findByFechaBetween(inicio, fin).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public HistorialClinicoDto createHistorialClinico(HistorialClinicoDto historialClinicoDto) {
        Mascota mascota = mascotaRepository.findById(historialClinicoDto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", historialClinicoDto.getMascotaId()));
        
        Usuario veterinario = usuarioRepository.findById(historialClinicoDto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", historialClinicoDto.getUsuarioId()));
        
        HistorialClinico historialClinico = new HistorialClinico();
        historialClinico.setFecha(historialClinicoDto.getFecha() != null ? historialClinicoDto.getFecha() : LocalDateTime.now());
        historialClinico.setDiagnostico(historialClinicoDto.getDiagnostico());
        historialClinico.setTratamiento(historialClinicoDto.getTratamiento());
        historialClinico.setObservaciones(historialClinicoDto.getObservaciones());
        historialClinico.setMascota(mascota);
        historialClinico.setUsuario(veterinario);
        
        if (historialClinicoDto.getCitaId() != null) {
            Cita cita = citaRepository.findById(historialClinicoDto.getCitaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", historialClinicoDto.getCitaId()));
            historialClinico.setCita(cita);
        }
        
        HistorialClinico savedHistorialClinico = historialClinicoRepository.save(historialClinico);
        return convertToDto(savedHistorialClinico);
    }
    
    @Transactional
    public HistorialClinicoDto updateHistorialClinico(Long id, HistorialClinicoDto historialClinicoDto) {
        try {
            HistorialClinico historialClinico = historialClinicoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("HistorialClinico", "id", id));
            
            historialClinico.setFecha(historialClinicoDto.getFecha());
            historialClinico.setDiagnostico(historialClinicoDto.getDiagnostico());
            historialClinico.setTratamiento(historialClinicoDto.getTratamiento());
            historialClinico.setObservaciones(historialClinicoDto.getObservaciones());
            
            if (historialClinicoDto.getMascotaId() != null && 
                    (historialClinico.getMascota() == null || !historialClinico.getMascota().getId().equals(historialClinicoDto.getMascotaId()))) {
                Mascota mascota = mascotaRepository.findById(historialClinicoDto.getMascotaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", historialClinicoDto.getMascotaId()));
                historialClinico.setMascota(mascota);
            }
            
            if (historialClinicoDto.getUsuarioId() != null && 
                    (historialClinico.getUsuario() == null || !historialClinico.getUsuario().getId().equals(historialClinicoDto.getUsuarioId()))) {
                Usuario veterinario = usuarioRepository.findById(historialClinicoDto.getUsuarioId())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", historialClinicoDto.getUsuarioId()));
                historialClinico.setUsuario(veterinario);
            }
            
            if (historialClinicoDto.getCitaId() != null && 
                    (historialClinico.getCita() == null || !historialClinico.getCita().getId().equals(historialClinicoDto.getCitaId()))) {
                Cita cita = citaRepository.findById(historialClinicoDto.getCitaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", historialClinicoDto.getCitaId()));
                historialClinico.setCita(cita);
            }
            
            HistorialClinico updatedHistorialClinico = historialClinicoRepository.save(historialClinico);
            return convertToDto(updatedHistorialClinico);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteHistorialClinico(Long id) {
        if (!historialClinicoRepository.existsById(id)) {
            return false;
        }
        historialClinicoRepository.deleteById(id);
        return true;
    }
    
    private HistorialClinicoDto convertToDto(HistorialClinico historialClinico) {
        HistorialClinicoDto dto = new HistorialClinicoDto();
        dto.setId(historialClinico.getId());
        dto.setFecha(historialClinico.getFecha());
        dto.setDiagnostico(historialClinico.getDiagnostico());
        dto.setTratamiento(historialClinico.getTratamiento());
        dto.setObservaciones(historialClinico.getObservaciones());
        
        if (historialClinico.getMascota() != null) {
            dto.setMascotaId(historialClinico.getMascota().getId());
            dto.setMascotaNombre(historialClinico.getMascota().getNombre());
            
            if (historialClinico.getMascota().getCliente() != null) {
                dto.setClienteId(historialClinico.getMascota().getCliente().getId());
                dto.setClienteNombre(historialClinico.getMascota().getCliente().getNombre() + " " + 
                                     historialClinico.getMascota().getCliente().getApellido());
            }
        }
        
        if (historialClinico.getUsuario() != null) {
            dto.setUsuarioId(historialClinico.getUsuario().getId());
            dto.setUsuarioNombre(historialClinico.getUsuario().getNombreUsuario());
        }
        
        if (historialClinico.getCita() != null) {
            dto.setCitaId(historialClinico.getCita().getId());
        }
        
        return dto;
    }
}
