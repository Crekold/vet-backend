package com.backend.vet.controller;

import com.backend.vet.dto.LoginRequestDto;
import com.backend.vet.dto.LoginResponseDto;
import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.security.jwt.JwtUtils;
import com.backend.vet.service.UsuarioService;
import com.backend.vet.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para la autenticación y registro de usuarios")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Operation(summary = "Iniciar sesión", description = "${api.auth.login.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "401", description = "${api.response-codes.unauthorized.description}",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "Credenciales de usuario", required = true)
            @Valid @RequestBody LoginRequestDto loginRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getNombreUsuario(), loginRequest.getContrasena())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        LoginResponseDto responseDto = LoginResponseDto.builder()
                .mensaje("Login exitoso")
                .token(jwt)
                .tipo("Bearer")
                .nombreUsuario(userDetails.getUsername())
                .roles(userDetails.getAuthorities())
                .build();
        
        return ResponseUtil.ok(responseDto);
    }
    
    @Operation(summary = "Registrar usuario", description = "${api.auth.register.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "Datos del usuario a registrar", required = true)
            @Valid @RequestBody UsuarioDto usuarioDto) {
        
        if (usuarioService.getUsuarioByNombreUsuario(usuarioDto.getNombreUsuario()).isPresent()) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
        
        UsuarioDto nuevoUsuario = usuarioService.createUsuario(usuarioDto);
        return ResponseUtil.ok(nuevoUsuario);
    }
}
