package pe.edu.upeu.lp2_gp2.services;

import pe.edu.upeu.lp2_gp2.dto.PagoDTO;
import pe.edu.upeu.lp2_gp2.dto.PagoRequestDTO;

import java.util.List;

public interface IPagoService {
    List<PagoDTO> listar(Long reservaId);
    PagoDTO registrar(PagoRequestDTO dto);
    PagoDTO resolver(Long pagoId, String estado);
}
