package com.backend.vet.service;

import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.exception.BadRequestException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


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

        // Act: Actualizar contraseña
        UsuarioDto updateDto = createValidUsuarioDto("histUser2", "hist2@test.com", secondPassword); // Mismo user/email, nueva pass
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
        UsuarioDto updateDto1 = createValidUsuarioDto("histUser3", "hist3@test.com", secondPassword);
        usuarioService.updateUsuario(userId, updateDto1); // Ahora la contraseña es secondPassword

        // Act & Assert: Intentar volver a la contraseña inicial (que está en el historial reciente)
        UsuarioDto updateDto2 = createValidUsuarioDto("histUser3", "hist3@test.com", initialPassword);

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
        UsuarioDto updateDto = createValidUsuarioDto("histUser5", "hist5@test.com", secondPassword);
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
            UsuarioDto updateDto = createValidUsuarioDto(user.getNombreUsuario(), user.getCorreo(), newPassword);
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
}
