package com.backend.vet.service;

import com.backend.vet.dto.MascotaDto;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Cliente;
import com.backend.vet.model.Mascota;
import com.backend.vet.repository.ClienteRepository;
import com.backend.vet.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaService {
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<MascotaDto> getAllMascotas() {
        return mascotaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public MascotaDto getMascotaById(Long id) {
        return mascotaRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public List<MascotaDto> getMascotasByClienteId(Long clienteId) {
        return mascotaRepository.findByClienteId(clienteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MascotaDto createMascota(MascotaDto mascotaDto) {
        Cliente cliente = clienteRepository.findById(mascotaDto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", mascotaDto.getClienteId()));
        
        Mascota mascota = new Mascota();
        mascota.setNombre(mascotaDto.getNombre());
        mascota.setEspecie(mascotaDto.getEspecie());
        mascota.setRaza(mascotaDto.getRaza());
        mascota.setFechaNacimiento(mascotaDto.getFechaNacimiento());
        mascota.setSexo(mascotaDto.getSexo());
        mascota.setCliente(cliente);
        
        Mascota savedMascota = mascotaRepository.save(mascota);
        return convertToDto(savedMascota);
    }
    
    @Transactional
    public MascotaDto updateMascota(Long id, MascotaDto mascotaDto) {
        try {
            Mascota mascota = mascotaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", id));
            
            mascota.setNombre(mascotaDto.getNombre());
            mascota.setEspecie(mascotaDto.getEspecie());
            mascota.setRaza(mascotaDto.getRaza());
            mascota.setFechaNacimiento(mascotaDto.getFechaNacimiento());
            mascota.setSexo(mascotaDto.getSexo());
            
            if (mascotaDto.getClienteId() != null && 
                    (mascota.getCliente() == null || !mascota.getCliente().getId().equals(mascotaDto.getClienteId()))) {
                Cliente cliente = clienteRepository.findById(mascotaDto.getClienteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", mascotaDto.getClienteId()));
                mascota.setCliente(cliente);
            }
            
            Mascota updatedMascota = mascotaRepository.save(mascota);
            return convertToDto(updatedMascota);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteMascota(Long id) {
        if (!mascotaRepository.existsById(id)) {
            return false;
        }
        mascotaRepository.deleteById(id);
        return true;
    }
    
    private MascotaDto convertToDto(Mascota mascota) {
        MascotaDto dto = new MascotaDto();
        dto.setId(mascota.getId());
        dto.setNombre(mascota.getNombre());
        dto.setEspecie(mascota.getEspecie());
        dto.setRaza(mascota.getRaza());
        dto.setFechaNacimiento(mascota.getFechaNacimiento());
        dto.setSexo(mascota.getSexo());
        
        if (mascota.getCliente() != null) {
            dto.setClienteId(mascota.getCliente().getId());
            dto.setClienteNombre(mascota.getCliente().getNombre());
            dto.setClienteApellido(mascota.getCliente().getApellido());
        }
        
        return dto;
    }
}
