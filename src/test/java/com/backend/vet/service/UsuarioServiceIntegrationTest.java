package com.backend.vet.service;

import com.backend.vet.dto.LoginRequestDto;
import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.model.PasswordHistory;
import com.backend.vet.model.Role;
import com.backend.vet.model.Usuario;
import com.backend.vet.repository.PasswordHistoryRepository;
import com.backend.vet.repository.RoleRepository;
import com.backend.vet.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

// Importar UsuarioUpdateDto
import com.backend.vet.dto.UsuarioUpdateDto;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Asegura que cada prueba se ejecute en una transacción y se haga rollback
class UsuarioServiceIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.security.password-history-size:5}")
    private int PASSWORD_HISTORY_SIZE;

    private Role defaultRole;
    private final String initialPassword = "Password123!";
    private final String secondPassword = "Password456@";
    private final String thirdPassword = "Password789#";


    @BeforeEach
    void setUp() {
        // Crear un rol si no existe para las pruebas
        defaultRole = roleRepository.findByNombre("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setNombre("ROLE_USER");
            return roleRepository.save(role);
        });
    }

    private UsuarioDto createValidUsuarioDto(String username, String email, String password) {
        UsuarioDto dto = new UsuarioDto();
        dto.setNombreUsuario(username);
        dto.setCorreo(email);
        dto.setContrasena(password);
        dto.setRolId(defaultRole.getId());
        dto.setEspecialidad("General");
        return dto;
    }

    // Helper para crear UsuarioUpdateDto
    private UsuarioUpdateDto createValidUsuarioUpdateDto(String username, String email, String password, Long rolId, String especialidad) {
        UsuarioUpdateDto dto = new UsuarioUpdateDto();
        dto.setNombreUsuario(username);
        dto.setCorreo(email);
        dto.setContrasena(password); // La contraseña es opcional, pero la incluimos para los tests de cambio de contraseña
        dto.setRolId(rolId);
        dto.setEspecialidad(especialidad);
        return dto;
    }

    @Test
    void createUsuario_shouldAddPasswordToHistory() {
        UsuarioDto dto = createValidUsuarioDto("histUser1", "hist1@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);

        Optional<Usuario> userOpt = usuarioRepository.findById(createdUserDto.getId());
        assertTrue(userOpt.isPresent());
        Usuario user = userOpt.get();

        List<PasswordHistory> history = passwordHistoryRepository.findByUsuarioOrderByCreationDateDesc(user, PageRequest.of(0, 10));

        assertThat(history).hasSize(1);
        assertTrue(passwordEncoder.matches(initialPassword, history.get(0).getPasswordHash()));
        assertEquals(user.getContrasenaHash(), history.get(0).getPasswordHash());
    }

    @Test
    void updateUsuarioPassword_shouldAddNewPasswordToHistory() {
        // Arrange: Crear usuario inicial
        UsuarioDto dto = createValidUsuarioDto("histUser2", "hist2@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        Long userId = createdUserDto.getId();

        // Act: Actualizar contraseña usando UsuarioUpdateDto
        UsuarioUpdateDto updateDto = createValidUsuarioUpdateDto("histUser2", "hist2@test.com", secondPassword, defaultRole.getId(), "General");
        usuarioService.updateUsuario(userId, updateDto);

        // Assert
        Optional<Usuario> userOpt = usuarioRepository.findById(userId);
        assertTrue(userOpt.isPresent());
        Usuario user = userOpt.get();

        // Verificar que la contraseña del usuario se actualizó
        assertTrue(passwordEncoder.matches(secondPassword, user.getContrasenaHash()));

        // Verificar historial
        List<PasswordHistory> history = passwordHistoryRepository.findByUsuarioOrderByCreationDateDesc(user, PageRequest.of(0, 10));
        assertThat(history).hasSize(2); // Contraseña inicial + nueva contraseña
        // La más reciente debe ser la segunda contraseña
        assertTrue(passwordEncoder.matches(secondPassword, history.get(0).getPasswordHash()));
        assertEquals(user.getContrasenaHash(), history.get(0).getPasswordHash());
        // La anterior debe ser la inicial
        assertTrue(passwordEncoder.matches(initialPassword, history.get(1).getPasswordHash()));
    }

    @Test
    void updateUsuarioPassword_shouldFailWhenReusingRecentPassword() {
        // Arrange: Crear usuario y cambiar contraseña una vez
        UsuarioDto dto = createValidUsuarioDto("histUser3", "hist3@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        Long userId = createdUserDto.getId();
        // Usar UsuarioUpdateDto para la primera actualización
        UsuarioUpdateDto updateDto1 = createValidUsuarioUpdateDto("histUser3", "hist3@test.com", secondPassword, defaultRole.getId(), "General");
        usuarioService.updateUsuario(userId, updateDto1); // Ahora la contraseña es secondPassword

        // Act & Assert: Intentar volver a la contraseña inicial (que está en el historial reciente)
        // Usar UsuarioUpdateDto para el intento fallido
        UsuarioUpdateDto updateDto2 = createValidUsuarioUpdateDto("histUser3", "hist3@test.com", initialPassword, defaultRole.getId(), "General");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            usuarioService.updateUsuario(userId, updateDto2);
        });

        // Verificar mensaje de error
        assertTrue(exception.getMessage().contains("no puede ser igual a una de las últimas"));

        // Verificar que la contraseña del usuario NO cambió
        Optional<Usuario> userOpt = usuarioRepository.findById(userId);
        assertTrue(userOpt.isPresent());
        assertTrue(passwordEncoder.matches(secondPassword, userOpt.get().getContrasenaHash())); // Debe seguir siendo secondPassword

        // Verificar que el historial no creció innecesariamente
        List<PasswordHistory> history = passwordHistoryRepository.findByUsuarioOrderByCreationDateDesc(userOpt.get(), PageRequest.of(0, 10));
        assertThat(history).hasSize(2); // Debe seguir teniendo 2 entradas
    }

     @Test
    void resetPassword_shouldAddPasswordToHistory() {
        // Arrange: Crear usuario y generar token
        UsuarioDto dto = createValidUsuarioDto("histUser4", "hist4@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        String token = usuarioService.createPasswordResetToken("hist4@test.com");

        // Act: Restablecer contraseña
        usuarioService.resetPassword(token, secondPassword);

        // Assert
        Optional<Usuario> userOpt = usuarioRepository.findByCorreo("hist4@test.com");
        assertTrue(userOpt.isPresent());
        Usuario user = userOpt.get();

        // Verificar contraseña actualizada y token limpiado
        assertTrue(passwordEncoder.matches(secondPassword, user.getContrasenaHash()));
        assertNull(user.getResetToken());
        assertNull(user.getResetTokenExpiry());

        // Verificar historial
        List<PasswordHistory> history = passwordHistoryRepository.findByUsuarioOrderByCreationDateDesc(user, PageRequest.of(0, 10));
        assertThat(history).hasSize(2); // Inicial + reset
        assertTrue(passwordEncoder.matches(secondPassword, history.get(0).getPasswordHash()));
    }

    @Test
    void resetPassword_shouldFailWhenReusingRecentPassword() {
        // Arrange: Crear usuario, cambiar contraseña, generar token
        UsuarioDto dto = createValidUsuarioDto("histUser5", "hist5@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        Long userId = createdUserDto.getId();
        // Usar UsuarioUpdateDto para cambiar la contraseña
        UsuarioUpdateDto updateDto = createValidUsuarioUpdateDto("histUser5", "hist5@test.com", secondPassword, defaultRole.getId(), "General");
        usuarioService.updateUsuario(userId, updateDto); // Contraseña actual es secondPassword
        String token = usuarioService.createPasswordResetToken("hist5@test.com");

        // Act & Assert: Intentar restablecer a la contraseña inicial (en historial)
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            usuarioService.resetPassword(token, initialPassword);
        });

        assertTrue(exception.getMessage().contains("no puede ser igual a una de las últimas"));

        // Verificar que la contraseña NO cambió
        Optional<Usuario> userOpt = usuarioRepository.findById(userId);
        assertTrue(userOpt.isPresent());
        assertTrue(passwordEncoder.matches(secondPassword, userOpt.get().getContrasenaHash())); // Sigue siendo secondPassword
    }

    @Test
    void addPasswordToHistory_shouldPruneOldEntries() {
         // Arrange: Crear usuario
        UsuarioDto dto = createValidUsuarioDto("histUser6", "hist6@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        Long userId = createdUserDto.getId();
        Optional<Usuario> userOpt = usuarioRepository.findById(userId);
        assertTrue(userOpt.isPresent());
        Usuario user = userOpt.get();

        String currentPassword = initialPassword;

        // Act: Cambiar contraseña PASSWORD_HISTORY_SIZE veces más (total HISTORY_SIZE + 1)
        for (int i = 0; i < PASSWORD_HISTORY_SIZE; i++) {
            String newPassword = "NewPass" + i + "$";
            // Usar UsuarioUpdateDto para actualizar
            // Corregir aquí: Usar defaultRole.getId() en lugar de user.getRole().getId()
            UsuarioUpdateDto updateDto = createValidUsuarioUpdateDto(user.getNombreUsuario(), user.getCorreo(), newPassword, defaultRole.getId(), user.getEspecialidad());
            usuarioService.updateUsuario(userId, updateDto);
            currentPassword = newPassword; // Actualizar para la siguiente iteración si fuera necesario
        }

         // Assert
        userOpt = usuarioRepository.findById(userId); // Recargar usuario
        assertTrue(userOpt.isPresent());
        user = userOpt.get();

        // Verificar que la contraseña actual es la última establecida
        assertTrue(passwordEncoder.matches(currentPassword, user.getContrasenaHash()));

        // Verificar que el tamaño del historial es exactamente PASSWORD_HISTORY_SIZE
        long historyCount = passwordHistoryRepository.countByUsuario(user);
        assertEquals(PASSWORD_HISTORY_SIZE, historyCount);

        // Verificar que la contraseña inicial (la más antigua) ya NO está en el historial
        List<PasswordHistory> history = passwordHistoryRepository.findByUsuarioOrderByCreationDateDesc(user, PageRequest.of(0, PASSWORD_HISTORY_SIZE + 5));
        for (PasswordHistory entry : history) {
            assertFalse(passwordEncoder.matches(initialPassword, entry.getPasswordHash()),
                    "La contraseña inicial no debería estar en el historial después de podar.");
        }
    }

    @Test
    void deleteUsuario_shouldMarkUserAsInactive() {
        // Arrange: Crear un usuario
        UsuarioDto dto = createValidUsuarioDto("deleteUser", "delete@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        Long userId = createdUserDto.getId();

        // Act: Eliminar (lógicamente) el usuario
        boolean result = usuarioService.deleteUsuario(userId);

        // Assert
        assertTrue(result, "El método deleteUsuario debería retornar true");

        // Verificar que el usuario existe pero está inactivo
        Optional<Usuario> userOpt = usuarioRepository.findById(userId);
        assertTrue(userOpt.isPresent(), "El usuario aún debe existir en la base de datos");
        assertFalse(userOpt.get().isActivo(), "El campo 'activo' del usuario debe ser false");

        // Opcional: Verificar que no se puede obtener por el servicio getUsuarioById si este filtrara por activos
        // assertNull(usuarioService.getUsuarioById(userId)); 
        
        // Verificar que no se puede cargar con UserDetailsServiceImpl
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("deleteUser");
        });
    }

    @Test
    void deleteUsuario_shouldReturnFalseIfAlreadyInactive() {
        // Arrange: Crear un usuario y marcarlo como inactivo
        UsuarioDto dto = createValidUsuarioDto("inactiveUser", "inactive@test.com", initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        Long userId = createdUserDto.getId();
        usuarioService.deleteUsuario(userId); // Primera eliminación (lógica)

        // Act: Intentar eliminar de nuevo
        boolean result = usuarioService.deleteUsuario(userId);

        // Assert
        assertFalse(result, "El método deleteUsuario debería retornar false si ya está inactivo");
    }

    @Test
    void deleteUsuario_shouldThrowNotFoundExceptionForNonExistingUser() {
        // Arrange: ID de usuario que no existe
        Long nonExistingUserId = 9999L;

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.deleteUsuario(nonExistingUserId);
        });
    }

    // ... Asegúrate de adaptar otras pruebas como las de login para considerar el estado 'activo' ...

    // Ejemplo: Modificar prueba de login para usuario inactivo
    @Test
    void login_shouldFailForInactiveUser() {
        // Arrange: Crear usuario y marcarlo como inactivo
        String username = "inactiveLoginUser";
        String email = "inactiveLogin@test.com";
        UsuarioDto dto = createValidUsuarioDto(username, email, initialPassword);
        UsuarioDto createdUserDto = usuarioService.createUsuario(dto);
        usuarioService.deleteUsuario(createdUserDto.getId()); // Marcar como inactivo

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setNombreUsuario(username);
        loginRequest.setContrasena(initialPassword);

        // Act & Assert en AuthController (simulado o con MockMvc si tienes tests de controlador)
        // Aquí simulamos la lógica del controlador:
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioEntityByNombreUsuario(username);
        assertTrue(usuarioOpt.isPresent());
        assertFalse(usuarioOpt.get().isActivo()); // Verificar que está inactivo

        // La verificación en AuthController debería ocurrir antes de llamar a authenticationManager
        // Por lo tanto, esperamos un error 401 o similar directamente, no una AuthenticationException estándar
        // Si estuvieras probando el AuthController con MockMvc, verificarías el status 401.
        // Como esto es un test de servicio, verificamos el estado y que UserDetailsService lo rechaza.
         assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
    }
}
