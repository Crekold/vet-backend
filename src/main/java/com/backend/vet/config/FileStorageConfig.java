package com.backend.vet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {
    private String uploadDir = "uploads";
    private String historialDir = "historiales";
    
    public String getUploadDir() {
        return uploadDir;
    }
    
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
    
    public String getHistorialDir() {
        return historialDir;
    }
    
    public void setHistorialDir(String historialDir) {
        this.historialDir = historialDir;
    }
    
    public Path getHistorialStoragePath() {
        return Paths.get(uploadDir, historialDir).toAbsolutePath().normalize();
    }
}
