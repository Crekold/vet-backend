package com.backend.vet.ezequielgomez;

import com.backend.vet.controller.AuthController;
import com.backend.vet.dto.LoginRequestDto;
import com.backend.vet.dto.LoginResponseDto;
import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.dto.ResetPasswordDto;
import com.backend.vet.model.Usuario;
import com.backend.vet.model.Role;
import com.backend.vet.security.jwt.JwtUtils;
import com.backend.vet.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para AuthController
 * Autor: Ezequiel Gomez
 * Estructura: Arrange-Act-Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para AuthController")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDto loginRequestDto;
    private Usuario usuarioActivo;
    private Role rolVeterinario;

    @BeforeEach
    void setUp() {
        // Configuración común para todas las pruebas
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setNombreUsuario("testuser");
        loginRequestDto.setContrasena("password123");

        rolVeterinario = new Role();
        rolVeterinario.setId(1L);
        rolVeterinario.setNombre("VETERINARIO");

        usuarioActivo = new Usuario();
        usuarioActivo.setId(1L);
        usuarioActivo.setNombreUsuario("testuser");
        usuarioActivo.setActivo(true);
        usuarioActivo.setRol(rolVeterinario);
    }

    @Test
    @DisplayName("debería autenticar exitosamente un usuario con credenciales válidas")
    void deberiaAutenticarExitosamenteUsuarioConCredencialesValidas() {
        // 1. PREPARACIÓN
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password123")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_VETERINARIO")))
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(usuarioService.getUsuarioEntityByNombreUsuario("testuser"))
                .thenReturn(Optional.of(usuarioActivo));
        when(usuarioService.isAccountLocked("testuser")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mock-jwt-token");
        when(usuarioService.isPasswordExpired("testuser")).thenReturn(false);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<?> response = authController.authenticateUser(loginRequestDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponseDto);
        
        LoginResponseDto responseDto = (LoginResponseDto) response.getBody();
        assertEquals("Login exitoso", responseDto.getMensaje());
        assertEquals("mock-jwt-token", responseDto.getToken());
        assertEquals("Bearer", responseDto.getTipo());
        assertEquals("testuser", responseDto.getNombreUsuario());
        assertFalse(responseDto.getPasswordChangeRequired());
        
        verify(usuarioService, times(1)).processLoginSuccess("testuser");
    }

    @Test
    @DisplayName("debería rechazar autenticación cuando el usuario está inactivo")
    void deberiaRechazarAutenticacionCuandoUsuarioEstaInactivo() {
        // 1. PREPARACIÓN
        Usuario usuarioInactivo = new Usuario();
        usuarioInactivo.setId(1L);
        usuarioInactivo.setNombreUsuario("testuser");
        usuarioInactivo.setActivo(false);

        when(usuarioService.getUsuarioEntityByNombreUsuario("testuser"))
                .thenReturn(Optional.of(usuarioInactivo));

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<?> response = authController.authenticateUser(loginRequestDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuario inactivo o no encontrado.", response.getBody());
        
        verify(authenticationManager, never()).authenticate(any());
        verify(usuarioService, never()).processLoginSuccess(anyString());
    }

    @Test
    @DisplayName("debería procesar intento fallido cuando las credenciales son incorrectas")
    void deberiaProcesarIntentoFallidoCuandoCredencialesSonIncorrectas() {
        // 1. PREPARACIÓN
        when(usuarioService.getUsuarioEntityByNombreUsuario("testuser"))
                .thenReturn(Optional.of(usuarioActivo));
        when(usuarioService.isAccountLocked("testuser")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<?> response = authController.authenticateUser(loginRequestDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());
        
        verify(usuarioService, times(1)).processLoginFailure("testuser");
        verify(usuarioService, never()).processLoginSuccess(anyString());
    }

    @Test
    @DisplayName("debería registrar un nuevo usuario exitosamente")
    void deberiaRegistrarNuevoUsuarioExitosamente() {
        // 1. PREPARACIÓN
        UsuarioDto nuevoUsuarioDto = new UsuarioDto();
        nuevoUsuarioDto.setNombreUsuario("nuevouser");
        nuevoUsuarioDto.setCorreo("nuevo@test.com");
        nuevoUsuarioDto.setContrasena("Password123!");
        nuevoUsuarioDto.setRolId(1L);

        UsuarioDto usuarioCreado = new UsuarioDto();
        usuarioCreado.setId(1L);
        usuarioCreado.setNombreUsuario("nuevouser");
        usuarioCreado.setCorreo("nuevo@test.com");
        usuarioCreado.setActivo(true);

        when(usuarioService.createUsuario(any(UsuarioDto.class))).thenReturn(usuarioCreado);

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<?> response = authController.registerUser(nuevoUsuarioDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UsuarioDto);
        
        UsuarioDto responseDto = (UsuarioDto) response.getBody();
        assertEquals("nuevouser", responseDto.getNombreUsuario());
        assertEquals("nuevo@test.com", responseDto.getCorreo());
        assertTrue(responseDto.isActivo());
        
        verify(usuarioService, times(1)).createUsuario(any(UsuarioDto.class));
    }

    @Test
    @DisplayName("debería restablecer contraseña exitosamente con token válido")
    void deberiaRestablecerContraseniaExitosamenteConTokenValido() {
        // 1. PREPARACIÓN
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setToken("valid-token");
        resetPasswordDto.setNewPassword("NewPassword123!");

        doNothing().when(usuarioService)
                .resetPassword("valid-token", "NewPassword123!");

        // 2. LÓGICA DE LA PRUEBA
        ResponseEntity<?> response = authController.resetPassword(resetPasswordDto);

        // 3. VERIFICACIÓN CON ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("exitosamente"));
        
        verify(usuarioService, times(1))
                .resetPassword("valid-token", "NewPassword123!");
    }
}
