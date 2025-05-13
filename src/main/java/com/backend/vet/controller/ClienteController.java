package com.backend.vet.controller;

import com.backend.vet.dto.ClienteDto;
import com.backend.vet.service.ClienteService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "API para la gestión de clientes")
public class ClienteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    
    @Autowired
    private ClienteService clienteService;
    
    @Operation(summary = "Obtener todos los clientes", description = "${api.cliente.getAll.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "403", description = "${api.response-codes.forbidden.description}")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<List<ClienteDto>> getAllClientes() {
        logger.info("Consultando todos los clientes");
        List<ClienteDto> clientes = clienteService.getAllClientes();
        logger.debug("Se encontraron {} clientes en total", clientes.size());
        return ResponseUtil.ok(clientes);
    }
    
    @Operation(summary = "Obtener cliente por ID", description = "${api.cliente.getById.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<ClienteDto> getClienteById(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long id) {
        logger.info("Buscando cliente con ID: {}", id);
        ClienteDto cliente = clienteService.getClienteById(id);
        if (cliente == null) {
            logger.warn("No se encontró el cliente con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.debug("Cliente encontrado con ID: {}", id);
        return ResponseUtil.ok(cliente);
    }
    
    @Operation(summary = "Crear nuevo cliente", description = "${api.cliente.create.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE_CREATE')")
    public ResponseEntity<ClienteDto> createCliente(
            @Parameter(description = "Datos del nuevo cliente", required = true)
            @Valid @RequestBody ClienteDto clienteDto) {
        logger.info("Creando nuevo cliente: {}", clienteDto.getNombre());
        try {
            ClienteDto created = clienteService.createCliente(clienteDto);
            logger.info("Cliente creado exitosamente con ID: {}", created.getId());
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(location).body(created);
        } catch (Exception e) {
            logger.error("Error al crear cliente: {}", e.getMessage());
            throw e;
        }
    }
    
    @Operation(summary = "Actualizar cliente", description = "${api.cliente.update.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_UPDATE')")
    public ResponseEntity<ClienteDto> updateCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del cliente", required = true)
            @Valid @RequestBody ClienteDto clienteDto) {
        logger.info("Actualizando cliente con ID: {}", id);
        ClienteDto updatedCliente = clienteService.updateCliente(id, clienteDto);
        if (updatedCliente == null) {
            logger.warn("No se pudo actualizar el cliente con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Cliente actualizado exitosamente con ID: {}", id);
        return ResponseUtil.ok(updatedCliente);
    }
    
    @Operation(summary = "Eliminar cliente", description = "${api.cliente.delete.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.response-codes.no-content.description}"),
        @ApiResponse(responseCode = "404", description = "${api.response-codes.not-found.description}")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_DELETE')")
    public ResponseEntity<Void> deleteCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long id) {
        logger.info("Eliminando cliente con ID: {}", id);
        boolean deleted = clienteService.deleteCliente(id);
        if (!deleted) {
            logger.warn("No se pudo eliminar el cliente con ID: {}", id);
            return ResponseUtil.notFound();
        }
        logger.info("Cliente eliminado exitosamente con ID: {}", id);
        return ResponseUtil.deleteResponse(deleted);
    }
}
