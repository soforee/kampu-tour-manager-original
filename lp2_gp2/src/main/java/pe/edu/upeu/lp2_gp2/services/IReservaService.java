package pe.edu.upeu.lp2_gp2.services;

import pe.edu.upeu.lp2_gp2.dto.PasajeroReservaDTO;
import pe.edu.upeu.lp2_gp2.dto.ReservaDTO;
import pe.edu.upeu.lp2_gp2.dto.ReservaRequestDTO;

import java.util.List;

public interface IReservaService {
    List<ReservaDTO> listarTodo();
    ReservaDTO buscarPorId(Long id);
    ReservaDTO crear(ReservaRequestDTO dto);
    ReservaDTO confirmar(Long id);
    ReservaDTO completar(Long id);
    ReservaDTO anular(Long id, Long usuarioId, String formaReembolso);
    List<PasajeroReservaDTO> listarPasajeros(Long reservaId);
}
