package pe.edu.upeu.lp2_gp2.services;

import pe.edu.upeu.lp2_gp2.dto.GuiaDTO;
import pe.edu.upeu.lp2_gp2.dto.GuiaRequestDTO;

import java.util.List;

public interface IGuiaService {
    List<GuiaDTO> listarTodo();
    GuiaDTO buscarPorId(Long id);
    GuiaDTO crear(GuiaRequestDTO dto);
    GuiaDTO actualizar(Long id, GuiaRequestDTO dto);
    void eliminar(Long id);
}
