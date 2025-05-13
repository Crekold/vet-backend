package com.backend.vet.controller;

import com.backend.vet.dto.*; // Importar nuevos DTOs
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.exception.TokenExpiredException;
import com.backend.vet.model.Usuario; // Importar Usuario
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
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // Importar BadCredentialsException
import org.springframework.security.authentication.LockedException; // Importar LockedException (o usar HttpStatus.LOCKED)
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // Importar AuthenticationException
import org.springframework.security.core.GrantedAuthority; // Importar GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.security.authentication.DisabledException; // Importar si usas esta excepción en UserDetailsServiceImpl

import jakarta.validation.Valid;
import java.util.Optional; // Importar Optional
import java.util.Collection; // Importar Collection
import java.util.stream.Collectors; // Importar Collectors

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para la autenticación y registro de usuarios")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Logger
    
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
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "423", description = "Cuenta bloqueada temporalmente", // Locked status
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "Credenciales de usuario", required = true)
            @Valid @RequestBody LoginRequestDto loginRequest) {
        
        logger.info("Intento de inicio de sesión para usuario: {}", loginRequest.getNombreUsuario());
        
        // 0. Verificar si la cuenta está activa ANTES de verificar bloqueo o autenticar
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioEntityByNombreUsuario(loginRequest.getNombreUsuario());
        if (usuarioOpt.isPresent() && !usuarioOpt.get().isActivo()) {
            logger.warn("Intento de login rechazado - Usuario inactivo: {}", loginRequest.getNombreUsuario());
            // Puedes devolver 401 Unauthorized o 403 Forbidden, 401 es común para credenciales inválidas/problemas de cuenta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body("Usuario inactivo o no encontrado."); 
        }
        // Si el usuario no existe, la autenticación fallará más adelante de todos modos.

        // 1. Verificar si la cuenta está bloqueada ANTES de intentar autenticar
        if (usuarioService.isAccountLocked(loginRequest.getNombreUsuario())) {
            logger.warn("Intento de login rechazado - Usuario bloqueado: {}", loginRequest.getNombreUsuario());
             // Usar LockedException o devolver directamente 423 Locked
             // throw new LockedException("La cuenta está bloqueada temporalmente debido a múltiples intentos fallidos.");
             return ResponseEntity.status(HttpStatus.LOCKED)
                    .body("La cuenta está bloqueada temporalmente debido a múltiples intentos fallidos.");
        }

        Authentication authentication;
        try {
            // 2. Intentar autenticar
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getNombreUsuario(), loginRequest.getContrasena())
            );
            
            // 3. Si la autenticación es exitosa, resetear intentos fallidos
            usuarioService.processLoginSuccess(loginRequest.getNombreUsuario());
            logger.info("Inicio de sesión exitoso para usuario: {}", loginRequest.getNombreUsuario());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Obtener autoridades como Collection<String>
            Collection<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Obtener el nombre del rol desde el Optional<Usuario> ya obtenido
            String rolNombre = usuarioOpt.map(u -> u.getRol() != null ? u.getRol().getNombre() : null)
                                         .orElse(null); // Si usuarioOpt está vacío (no debería pasar aquí), rolNombre será null

            // 4. Verificar si la contraseña ha expirado
            boolean passwordExpired = usuarioService.isPasswordExpired(userDetails.getUsername());
            if (passwordExpired) {
                logger.info("Contraseña expirada para usuario: {}", userDetails.getUsername());
            }
            
            LoginResponseDto responseDto = LoginResponseDto.builder()
                    .mensaje("Login exitoso")
                    .token(jwt)
                    .tipo("Bearer")
                    .nombreUsuario(userDetails.getUsername())
                    .roles(authorities) // Usar la colección de Strings
                    .rolNombre(rolNombre) // Añadir rolNombre
                    .passwordChangeRequired(passwordExpired) // Añadir estado de expiración
                    .build();
            
            return ResponseUtil.ok(responseDto);

        } catch (BadCredentialsException e) {
            // 5. Si las credenciales son incorrectas, procesar intento fallido
            logger.warn("Intento de login fallido - Credenciales inválidas para usuario: {}", loginRequest.getNombreUsuario());
            // Solo procesar fallo si el usuario existe y está activo
            if (usuarioOpt.isPresent() && usuarioOpt.get().isActivo()) {
                usuarioService.processLoginFailure(loginRequest.getNombreUsuario());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        // } catch (DisabledException e) { // Capturar si UserDetailsServiceImpl lanza DisabledException
        //     logger.warn("Intento de login para usuario deshabilitado: {}", loginRequest.getNombreUsuario());
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario inactivo.");
        } catch (AuthenticationException e) { // Captura UsernameNotFoundException (incluye inactivos si se lanza desde UserDetailsServiceImpl) y otras
             // 6. Otras excepciones de autenticación (podría ser cuenta deshabilitada, etc.)
             logger.error("Error de autenticación para usuario {}: {}", loginRequest.getNombreUsuario(), e.getMessage());
             // No procesar fallo aquí si el usuario no existe o está inactivo (ya manejado arriba o por la excepción)
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Registrar usuario", description = "${api.auth.register.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"), // Cambiado a 201 Created
        @ApiResponse(responseCode = "400", description = "${api.response-codes.bad-request.description}",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "Datos del usuario a registrar", required = true)
            @Valid @RequestBody UsuarioDto usuarioDto) {
        
        logger.info("Iniciando registro de nuevo usuario: {}", usuarioDto.getNombreUsuario());
        // La validación de existencia y complejidad ahora está principalmente en el servicio
        try {
            UsuarioDto nuevoUsuario = usuarioService.createUsuario(usuarioDto);
            logger.info("Usuario registrado exitosamente: {}", nuevoUsuario.getNombreUsuario());
            // Devolver 201 Created en lugar de 200 OK para registro
            return ResponseUtil.created(nuevoUsuario); 
        } catch (BadRequestException e) {
            logger.warn("Error en registro de usuario {}: {}", usuarioDto.getNombreUsuario(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Nuevos Endpoints ---

    @Operation(summary = "Solicitar restablecimiento de contraseña", description = "Inicia el proceso para restablecer la contraseña olvidada enviando un token al correo del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Si el correo existe, se ha iniciado el proceso (no revela si el correo existe por seguridad)."),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado (opcional, podría devolverse 200 siempre por seguridad).")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Parameter(description = "Correo electrónico del usuario", required = true)
            @Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        logger.info("Solicitud de restablecimiento de contraseña para correo: {}", forgotPasswordDto.getEmail());
        try {
            String token = usuarioService.createPasswordResetToken(forgotPasswordDto.getEmail());
            // --- Aquí iría la lógica para enviar el correo con el token ---
            logger.debug("Token de restablecimiento generado para correo: {}", forgotPasswordDto.getEmail()); // Loguear token solo para depuración
            // No enviar el token en la respuesta en producción
            return ResponseEntity.ok("Si el correo está registrado, recibirás instrucciones para restablecer tu contraseña.");
        } catch (ResourceNotFoundException e) {
            // Por seguridad, es mejor devolver siempre OK para no revelar si un correo existe
             logger.warn("Intento de restablecimiento para correo no registrado: {}", forgotPasswordDto.getEmail());
             return ResponseEntity.ok("Si el correo está registrado, recibirás instrucciones para restablecer tu contraseña.");
        } catch (Exception e) {
            logger.error("Error al procesar solicitud de restablecimiento para correo {}: {}", 
                        forgotPasswordDto.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
        }
    }

    @Operation(summary = "Restablecer contraseña", description = "Establece una nueva contraseña utilizando un token de restablecimiento válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente."),
        @ApiResponse(responseCode = "400", description = "Token inválido, expirado o la nueva contraseña no cumple los requisitos.")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "Token de restablecimiento y nueva contraseña", required = true)
            @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        logger.info("Intento de restablecimiento de contraseña con token");
        try {
            usuarioService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
            logger.info("Contraseña restablecida exitosamente mediante token");
            return ResponseEntity.ok("Contraseña restablecida exitosamente.");
        } catch (ResourceNotFoundException | TokenExpiredException | BadRequestException e) { // Capturar excepciones específicas
            logger.warn("Fallo al restablecer contraseña: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al restablecer contraseña: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
        }
    }
}
