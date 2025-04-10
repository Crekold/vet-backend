package com.backend.vet.service;

import com.backend.vet.dto.ClienteDto;
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.Cliente;
import com.backend.vet.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<ClienteDto> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ClienteDto getClienteById(Long id) {
        return clienteRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    @Transactional
    public ClienteDto createCliente(ClienteDto clienteDto) {
        // Validar correo único si existe
        if (clienteDto.getCorreo() != null && !clienteDto.getCorreo().isEmpty() && 
            clienteRepository.existsByCorreo(clienteDto.getCorreo())) {
            throw new BadRequestException("El correo electrónico ya está registrado");
        }
        
        Cliente cliente = convertToEntity(clienteDto);
        Cliente savedCliente = clienteRepository.save(cliente);
        return convertToDto(savedCliente);
    }
    
    @Transactional
    public ClienteDto updateCliente(Long id, ClienteDto clienteDto) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
            
            // Validar correo único si se está actualizando
            if (clienteDto.getCorreo() != null && !clienteDto.getCorreo().isEmpty() && 
                !clienteDto.getCorreo().equals(cliente.getCorreo()) && 
                clienteRepository.existsByCorreo(clienteDto.getCorreo())) {
                throw new BadRequestException("El correo electrónico ya está registrado");
            }
            
            cliente.setNombre(clienteDto.getNombre());
            cliente.setApellido(clienteDto.getApellido());
            cliente.setTelefono(clienteDto.getTelefono());
            cliente.setCorreo(clienteDto.getCorreo());
            cliente.setDireccion(clienteDto.getDireccion());
            
            Cliente updatedCliente = clienteRepository.save(cliente);
            return convertToDto(updatedCliente);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        clienteRepository.deleteById(id);
        return true;
    }
    
    private ClienteDto convertToDto(Cliente cliente) {
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setCorreo(cliente.getCorreo());
        dto.setDireccion(cliente.getDireccion());
        return dto;
    }
    
    private Cliente convertToEntity(ClienteDto clienteDto) {
        Cliente cliente = new Cliente();
        if (clienteDto.getId() != null) {
            cliente.setId(clienteDto.getId());
        }
        cliente.setNombre(clienteDto.getNombre());
        cliente.setApellido(clienteDto.getApellido());
        cliente.setTelefono(clienteDto.getTelefono());
        cliente.setCorreo(clienteDto.getCorreo());
        cliente.setDireccion(clienteDto.getDireccion());
        return cliente;
    }
}
