package com.backend.vet.service;

import com.backend.vet.dto.ArchivoClinicoDto;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.ArchivoClinico;
import com.backend.vet.model.HistorialClinico;
import com.backend.vet.repository.ArchivoClinicoRepository;
import com.backend.vet.repository.HistorialClinicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchivoClinicoService {
    
    @Autowired
    private ArchivoClinicoRepository archivoClinicoRepository;
    
    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    public List<ArchivoClinicoDto> getAllArchivosClinico() {
        return archivoClinicoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ArchivoClinicoDto getArchivoClinicoById(Long id) {
        return archivoClinicoRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    public List<ArchivoClinicoDto> getArchivosByHistorialClinicoId(Long historialClinicoId) {
        return archivoClinicoRepository.findByHistorialClinicoId(historialClinicoId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ArchivoClinicoDto> getArchivosByMascotaId(Long mascotaId) {
        return archivoClinicoRepository.findByHistorialClinicoMascotaId(mascotaId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ArchivoClinicoDto> getArchivosByNombre(String nombreArchivo) {
        return archivoClinicoRepository.findByNombreArchivoContaining(nombreArchivo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ArchivoClinicoDto> getArchivosByTipoMime(String tipoMime) {
        return archivoClinicoRepository.findByTipoMime(tipoMime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ArchivoClinicoDto createArchivoClinico(MultipartFile file, Long historialClinicoId) {
        HistorialClinico historialClinico = historialClinicoRepository.findById(historialClinicoId)
                .orElseThrow(() -> new ResourceNotFoundException("HistorialClinico", "id", historialClinicoId));
        
        // Almacenar el archivo en el sistema de archivos usando el FileStorageService
        String fileName = fileStorageService.storeFile(file);
        String fileUrl = fileStorageService.getFileUrl(fileName);
        
        // Crear registro en la base de datos
        ArchivoClinico archivoClinico = new ArchivoClinico();
        archivoClinico.setNombreArchivo(file.getOriginalFilename());
        archivoClinico.setUrl(fileUrl);
        archivoClinico.setTipoMime(file.getContentType());
        archivoClinico.setHistorialClinico(historialClinico);
        
        ArchivoClinico savedArchivoClinico = archivoClinicoRepository.save(archivoClinico);
        return convertToDto(savedArchivoClinico);
    }
    
    @Transactional
    public ArchivoClinicoDto updateArchivoClinico(Long id, ArchivoClinicoDto archivoClinicoDto) {
        try {
            ArchivoClinico archivoClinico = archivoClinicoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ArchivoClinico", "id", id));
            
            archivoClinico.setNombreArchivo(archivoClinicoDto.getNombreArchivo());
            
            if (archivoClinicoDto.getTipoMime() != null) {
                archivoClinico.setTipoMime(archivoClinicoDto.getTipoMime());
            }
            
            if (archivoClinicoDto.getHistorialClinicoId() != null &&
                    (archivoClinico.getHistorialClinico() == null || 
                     !archivoClinico.getHistorialClinico().getId().equals(archivoClinicoDto.getHistorialClinicoId()))) {
                HistorialClinico historialClinico = historialClinicoRepository.findById(archivoClinicoDto.getHistorialClinicoId())
                        .orElseThrow(() -> new ResourceNotFoundException("HistorialClinico", "id", archivoClinicoDto.getHistorialClinicoId()));
                archivoClinico.setHistorialClinico(historialClinico);
            }
            
            ArchivoClinico updatedArchivoClinico = archivoClinicoRepository.save(archivoClinico);
            return convertToDto(updatedArchivoClinico);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    
    @Transactional
    public boolean deleteArchivoClinico(Long id) {
        try {
            ArchivoClinico archivoClinico = archivoClinicoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ArchivoClinico", "id", id));
            
            // Extraer el nombre del archivo de la URL
            String fileName = archivoClinico.getUrl().substring(archivoClinico.getUrl().lastIndexOf("/") + 1);
            
            // Eliminar el archivo físico
            fileStorageService.deleteFile(fileName);
            
            // Eliminar el registro de la base de datos
            archivoClinicoRepository.delete(archivoClinico);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public Resource loadFileAsResource(String fileName) {
        return fileStorageService.loadFileAsResource(fileName);
    }
    
    private ArchivoClinicoDto convertToDto(ArchivoClinico archivoClinico) {
        ArchivoClinicoDto dto = new ArchivoClinicoDto();
        dto.setId(archivoClinico.getId());
        dto.setNombreArchivo(archivoClinico.getNombreArchivo());
        dto.setUrl(archivoClinico.getUrl());
        dto.setTipoMime(archivoClinico.getTipoMime());
        
        if (archivoClinico.getHistorialClinico() != null) {
            dto.setHistorialClinicoId(archivoClinico.getHistorialClinico().getId());
            
            // Información adicional para la respuesta
            if (archivoClinico.getHistorialClinico().getDiagnostico() != null) {
                String diagnostico = archivoClinico.getHistorialClinico().getDiagnostico();
                dto.setDiagnosticoResumen(diagnostico.length() > 50 ? diagnostico.substring(0, 47) + "..." : diagnostico);
            }
            
            if (archivoClinico.getHistorialClinico().getMascota() != null) {
                dto.setMascotaNombre(archivoClinico.getHistorialClinico().getMascota().getNombre());
            }
        }
        
        return dto;
    }
}
