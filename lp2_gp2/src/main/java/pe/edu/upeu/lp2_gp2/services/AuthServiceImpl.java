package pe.edu.upeu.lp2_gp2.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.lp2_gp2.config.TourProperties;
import pe.edu.upeu.lp2_gp2.dto.LoginRequestDTO;
import pe.edu.upeu.lp2_gp2.dto.LoginResponseDTO;
import pe.edu.upeu.lp2_gp2.dto.UsuarioDTO;
import pe.edu.upeu.lp2_gp2.entity.UsuarioSistema;
import pe.edu.upeu.lp2_gp2.exception.ForbiddenException;
import pe.edu.upeu.lp2_gp2.exception.UnauthorizedException;
import pe.edu.upeu.lp2_gp2.repository.UsuarioSistemaRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login contra USUARIOS_SISTEMA (username + password_hash BCrypt + estado ACTIVO).
 * Las sesiones se guardan en memoria: al reiniciar el backend hay que volver a iniciar sesión.
 */
@Service
public class AuthServiceImpl implements IAuthService {
    private record Sesion(Long usuarioId, Instant vence) {
    }

    private final UsuarioSistemaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Duration duracion;
    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public AuthServiceImpl(UsuarioSistemaRepository usuarioRepository, PasswordEncoder passwordEncoder, TourProperties props) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.duracion = Duration.ofHours(props.sesionHoras());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsuarioSistema u = usuarioRepository.findByUsernameIgnoreCase(dto.username().trim())
                .filter(x -> passwordEncoder.matches(dto.password(), x.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña incorrectos."));
        if (!"ACTIVO".equals(u.getEstado())) {
            throw new ForbiddenException("Tu usuario está inactivo. Contacta al administrador.");
        }
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sesiones.put(token, new Sesion(u.getUsuarioId(), Instant.now().plus(duracion)));
        sesiones.values().removeIf(s -> s.vence().isBefore(Instant.now())); // limpieza de vencidas
        return new LoginResponseDTO(token, UsuarioDTO.desde(u));
    }

    @Override
    public void logout(String token) {
        if (token != null) sesiones.remove(token);
    }

    @Override
    public Optional<Long> usuarioDeToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        Sesion s = sesiones.get(token);
        if (s == null) return Optional.empty();
        if (s.vence().isBefore(Instant.now())) {
            sesiones.remove(token);
            return Optional.empty();
        }
        return Optional.of(s.usuarioId());
    }
}
