package com.backend.vet.service;

import com.backend.vet.dto.UsuarioDto;
import com.backend.vet.exception.BadRequestException;
import com.backend.vet.exception.ResourceNotFoundException;
import com.backend.vet.exception.TokenExpiredException; // Necesita ser creada
import com.backend.vet.model.Role;
import com.backend.vet.model.Usuario;
import com.backend.vet.repository.RoleRepository;
import com.backend.vet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Importar Value
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom; // Importar SecureRandom
import java.time.LocalDateTime; // Importar LocalDateTime
import java.util.Base64; // Importar Base64
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern; // Importar Pattern
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    // Inyectar valores desde application.properties (o definir constantes)
    @Value("${app.security.max-failed-attempts:3}")
    private int MAX_FAILED_ATTEMPTS;

    @Value("${app.security.lock-duration-minutes:15}")
    private long LOCK_DURATION_MINUTES;

    @Value("${app.security.password-expiry-days:90}")
    private long PASSWORD_EXPIRY_DAYS;

    @Value("${app.security.reset-token-expiry-minutes:60}")
    private long RESET_TOKEN_EXPIRY_MINUTES;

    // Regex para validar complejidad (debe coincidir con el DTO)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom(); // Para generar tokens
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); // Para generar tokens

    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public UsuarioDto getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public Optional<Usuario> getUsuarioEntityByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public Optional<UsuarioDto> getUsuarioByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(this::convertToDto);
    }
    
    @Transactional
    public UsuarioDto createUsuario(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByNombreUsuario(usuarioDto.getNombreUsuario())) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByCorreo(usuarioDto.getCorreo())) {
            throw new BadRequestException("El correo electrónico ya está registrado");
        }

        // Validar complejidad de contraseña explícitamente aquí también
        validatePasswordComplexity(usuarioDto.getContrasena());
        
        Usuario usuario = new Usuario();
        
        usuario.setNombreUsuario(usuarioDto.getNombreUsuario());
        usuario.setCorreo(usuarioDto.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(usuarioDto.getContrasena()));
        usuario.setEspecialidad(usuarioDto.getEspecialidad());
        usuario.setPasswordLastChanged(LocalDateTime.now()); // Establecer fecha de cambio
        usuario.setFailedLoginAttempts(0); // Inicializar intentos fallidos
        
        Role role = roleRepository.findById(usuarioDto.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", usuarioDto.getRolId()));
        usuario.setRol(role);
        
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDto(savedUsuario);
    }
    
    @Transactional
    public UsuarioDto updateUsuario(Long id, UsuarioDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
            
        // Verificar si el nuevo nombreUsuario ya existe y no pertenece a este usuario
        if (!usuario.getNombreUsuario().equals(usuarioDto.getNombreUsuario()) && 
            usuarioRepository.existsByNombreUsuario(usuarioDto.getNombreUsuario())) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
            
        // Verificar si el nuevo correo ya existe y no pertenece a este usuario
        if (!usuario.getCorreo().equals(usuarioDto.getCorreo()) && 
            usuarioRepository.existsByCorreo(usuarioDto.getCorreo())) {
            throw new BadRequestException("El correo electrónico ya está registrado");
        }
            
        usuario.setNombreUsuario(usuarioDto.getNombreUsuario());
        usuario.setCorreo(usuarioDto.getCorreo());
        usuario.setEspecialidad(usuarioDto.getEspecialidad());
            
        if (usuarioDto.getContrasena() != null && !usuarioDto.getContrasena().isEmpty()) {
            validatePasswordComplexity(usuarioDto.getContrasena()); // Validar nueva contraseña
            usuario.setContrasenaHash(passwordEncoder.encode(usuarioDto.getContrasena()));
            usuario.setPasswordLastChanged(LocalDateTime.now()); // Actualizar fecha de cambio
        }
            
        if (usuarioDto.getRolId() != null && (usuario.getRol() == null || !usuario.getRol().getId().equals(usuarioDto.getRolId()))) {
            Role role = roleRepository.findById(usuarioDto.getRolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", usuarioDto.getRolId()));
            usuario.setRol(role);
        }
            
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return convertToDto(updatedUsuario);
    }
    
    @Transactional
    public boolean deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    // --- Nuevos métodos para gestión de contraseñas y bloqueo ---

    @Transactional
    public void processLoginFailure(String username) {
        Optional<Usuario> userOpt = usuarioRepository.findByNombreUsuario(username);
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLockExpirationTime(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            }
            usuarioRepository.save(user);
        }
    }

    @Transactional
    public void processLoginSuccess(String username) {
        Optional<Usuario> userOpt = usuarioRepository.findByNombreUsuario(username);
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            user.setFailedLoginAttempts(0);
            user.setLockExpirationTime(null);
            usuarioRepository.save(user);
        }
    }

    public boolean isAccountLocked(String username) {
        Optional<Usuario> userOpt = usuarioRepository.findByNombreUsuario(username);
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            return user.getLockExpirationTime() != null && user.getLockExpirationTime().isAfter(LocalDateTime.now());
        }
        return false; // O lanzar excepción si el usuario no existe
    }

    public boolean isPasswordExpired(String username) {
        Optional<Usuario> userOpt = usuarioRepository.findByNombreUsuario(username);
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            if (user.getPasswordLastChanged() == null) {
                return true; // Forzar cambio si nunca se ha establecido
            }
            return user.getPasswordLastChanged().plusDays(PASSWORD_EXPIRY_DAYS).isBefore(LocalDateTime.now());
        }
        return false; // O manejar como error
    }

    @Transactional
    public String createPasswordResetToken(String email) {
        Usuario user = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "correo", email));

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String token = base64Encoder.encodeToString(randomBytes);

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES));
        usuarioRepository.save(user);
        return token; // En una aplicación real, enviarías este token por correo
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Usuario user = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido o no encontrado"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setResetToken(null); // Limpiar token expirado
            user.setResetTokenExpiry(null);
            usuarioRepository.save(user);
            throw new TokenExpiredException("El token de restablecimiento ha expirado");
        }

        validatePasswordComplexity(newPassword); // Validar la nueva contraseña

        user.setContrasenaHash(passwordEncoder.encode(newPassword));
        user.setPasswordLastChanged(LocalDateTime.now());
        user.setResetToken(null); // Limpiar token después de usarlo
        user.setResetTokenExpiry(null);
        user.setFailedLoginAttempts(0); // Resetear intentos fallidos
        user.setLockExpirationTime(null); // Desbloquear cuenta si estaba bloqueada
        usuarioRepository.save(user);
    }

    private void validatePasswordComplexity(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BadRequestException("La contraseña no cumple con los requisitos de complejidad: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&).");
        }
        // Aquí se podría añadir la validación contra el historial de contraseñas si se implementa
    }
    
    private UsuarioDto convertToDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setCorreo(usuario.getCorreo());
        dto.setEspecialidad(usuario.getEspecialidad());
        // No establecemos la contraseña
        
        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getId());
            dto.setRolNombre(usuario.getRol().getNombre());
        }
        
        return dto;
    }
}
