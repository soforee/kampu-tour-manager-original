package pe.edu.upeu.lp2_gp2.dto;

public record LoginResponseDTO(
        String token,
        UsuarioDTO usuario
) {
}
