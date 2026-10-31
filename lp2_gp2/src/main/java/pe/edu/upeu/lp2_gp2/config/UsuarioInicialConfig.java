package pe.edu.upeu.lp2_gp2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.lp2_gp2.entity.UsuarioSistema;
import pe.edu.upeu.lp2_gp2.repository.UsuarioSistemaRepository;

/** Si USUARIOS_SISTEMA está vacía crea admin/admin123 y ventas/ventas123 para poder entrar. */
@Configuration
public class UsuarioInicialConfig {
    private static final Logger log = LoggerFactory.getLogger(UsuarioInicialConfig.class);

    @Bean
    CommandLineRunner crearUsuariosIniciales(UsuarioSistemaRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(nuevo("admin", encoder.encode("admin123"), "ADMIN"));
            repo.save(nuevo("ventas", encoder.encode("ventas123"), "VENTAS"));
            log.info("USUARIOS_SISTEMA vacía: se crearon los usuarios admin y ventas.");
        };
    }

    private static UsuarioSistema nuevo(String username, String hash, String rol) {
        UsuarioSistema u = new UsuarioSistema();
        u.setUsername(username);
        u.setPasswordHash(hash);
        u.setRol(rol);
        u.setEstado("ACTIVO");
        return u;
    }
}
