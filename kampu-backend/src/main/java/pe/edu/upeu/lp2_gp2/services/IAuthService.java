package pe.edu.upeu.lp2_gp2.services;

import pe.edu.upeu.lp2_gp2.dto.LoginRequestDTO;
import pe.edu.upeu.lp2_gp2.dto.LoginResponseDTO;

import java.util.Optional;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    void logout(String token);
    Optional<Long> usuarioDeToken(String token);
}
