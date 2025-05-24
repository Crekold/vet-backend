package com.backend.vet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class VetApplication {

	private static final Logger logger = LoggerFactory.getLogger(VetApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(VetApplication.class);
		Environment env = app.run(args).getEnvironment();
		
		logger.info("=== APLICACIÓN VETERINARIA INICIADA ===");
		logger.info("Perfiles activos: {}", String.join(", ", env.getActiveProfiles()));
		logger.info("Puerto: {}", env.getProperty("server.port", "8080"));
		logger.info("URL Base de datos: {}", env.getProperty("spring.datasource.url"));
		logger.info("=======================================");
	}

}
