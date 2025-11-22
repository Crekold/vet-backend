package com.backend.vet.service;

import com.backend.vet.dto.CitaDto;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Cita;
import com.backend.vet.model.Mascota;
import com.backend.vet.model.Usuario;
import com.backend.vet.repository.CitaRepository;
import com.backend.vet.repository.MascotaRepository;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<CitaDto> getAllCitas() {
        return citaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CitaDto getCitaById(Long id) {
        return citaRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public List<CitaDto> getCitasByMascotaId(Long mascotaId) {
        return citaRepository.findByMascotaId(mascotaId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CitaDto> getCitasByClienteId(Long clienteId) {
        return citaRepository.findByMascotaClienteId(clienteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CitaDto> getCitasByVeterinarioId(Long veterinarioId) {
        return citaRepository.findByUsuarioId(veterinarioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CitaDto> getCitasByFechaRango(LocalDate inicio, LocalDate fin) {
        return citaRepository.findByFechaBetween(inicio, fin).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CitaDto> getCitasByEstado(String estado) {
        return citaRepository.findByEstado(estado).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CitaDto createCita(CitaDto citaDto) {
        Mascota mascota = mascotaRepository.findById(citaDto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", citaDto.getMascotaId()));
        
        Usuario veterinario = usuarioRepository.findById(citaDto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", citaDto.getUsuarioId()));
        
        Cita cita = new Cita();
        cita.setFecha(citaDto.getFecha());
        cita.setHora(citaDto.getHora());
        cita.setMotivo(citaDto.getMotivo());
        cita.setEstado(citaDto.getEstado());
        cita.setMascota(mascota);
        cita.setUsuario(veterinario);
        
        Cita savedCita = citaRepository.save(cita);
        return convertToDto(savedCita);
    }
    
    @Transactional
    public CitaDto updateCita(Long id, CitaDto citaDto) {
        try {
            Cita cita = citaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
            
            cita.setFecha(citaDto.getFecha());
            cita.setHora(citaDto.getHora());
            cita.setMotivo(citaDto.getMotivo());
            cita.setEstado(citaDto.getEstado());
            
            if (citaDto.getMascotaId() != null && 
                    (cita.getMascota() == null || !cita.getMascota().getId().equals(citaDto.getMascotaId()))) {
                Mascota mascota = mascotaRepository.findById(citaDto.getMascotaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", citaDto.getMascotaId()));
                cita.setMascota(mascota);
            }
            
            if (citaDto.getUsuarioId() != null && 
                    (cita.getUsuario() == null || !cita.getUsuario().getId().equals(citaDto.getUsuarioId()))) {
                Usuario veterinario = usuarioRepository.findById(citaDto.getUsuarioId())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", citaDto.getUsuarioId()));
                cita.setUsuario(veterinario);
            }
            
            Cita updatedCita = citaRepository.save(cita);
            return convertToDto(updatedCita);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteCita(Long id) {
        if (!citaRepository.existsById(id)) {
            return false;
        }
        citaRepository.deleteById(id);
        return true;
    }
    
    private CitaDto convertToDto(Cita cita) {
        CitaDto dto = new CitaDto();
        dto.setId(cita.getId());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setMotivo(cita.getMotivo());
        dto.setEstado(cita.getEstado());
        
        // BUG INTRODUCIDO: Acceso directo sin verificar si mascota o usuario son nulos
        dto.setMascotaId(cita.getMascota().getId());
        dto.setMascotaNombre(cita.getMascota().getNombre());
        
        if (cita.getMascota().getCliente() != null) {
            dto.setClienteId(cita.getMascota().getCliente().getId());
            dto.setClienteNombre(cita.getMascota().getCliente().getNombre() + " " + 
                                cita.getMascota().getCliente().getApellido());
        }
        
        dto.setUsuarioId(cita.getUsuario().getId());
        dto.setUsuarioNombre(cita.getUsuario().getNombreUsuario());
        
        return dto;
    }

    /**
     * Cuenta el número de citas programadas para hoy
     * @return número de citas del día
     */
    public int countCitasDelDia() {
        return citaRepository.countCitasDelDia();
    }

    /**
     * Obtiene la lista de próximas citas
     * @return lista de citas próximas
     */
    public List<CitaDto> findProximasCitas() {
        LocalDate hoy = LocalDate.now();
        List<Cita> citas = citaRepository.findByFechaGreaterThanEqual(hoy);
        
        return citas.stream()
            .map(entity -> {
                CitaDto dto = convertToDto(entity);
                
                // asignar especie y raza
                dto.setEspecie(entity.getMascota().getEspecie());
                dto.setRaza(entity.getMascota().getRaza());
                
                return dto;
            })
            .collect(Collectors.toList());
    }
}
