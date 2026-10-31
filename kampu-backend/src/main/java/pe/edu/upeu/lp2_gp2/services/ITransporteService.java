package pe.edu.upeu.lp2_gp2.services;

import pe.edu.upeu.lp2_gp2.dto.TransporteDTO;
import pe.edu.upeu.lp2_gp2.dto.TransporteRequestDTO;

import java.util.List;

public interface ITransporteService {
    List<TransporteDTO> listarTodo();
    TransporteDTO buscarPorId(Long id);
    TransporteDTO crear(TransporteRequestDTO dto);
    TransporteDTO actualizar(Long id, TransporteRequestDTO dto);
    void eliminar(Long id);
}
