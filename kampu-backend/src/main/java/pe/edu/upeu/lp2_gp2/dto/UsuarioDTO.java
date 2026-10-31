package pe.edu.upeu.lp2_gp2.dto;

import pe.edu.upeu.lp2_gp2.entity.UsuarioSistema;

/** USUARIOS_SISTEMA sin password_hash. */
public record UsuarioDTO(
        Long usuarioId,
        String username,
        String rol,
        String estado
) {
    public static UsuarioDTO desde(UsuarioSistema u) {
        return new UsuarioDTO(u.getUsuarioId(), u.getUsername(), u.getRol(), u.getEstado());
    }
}
