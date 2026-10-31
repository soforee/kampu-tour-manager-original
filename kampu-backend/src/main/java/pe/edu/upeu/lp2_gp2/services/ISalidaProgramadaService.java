package pe.edu.upeu.lp2_gp2.services;

import pe.edu.upeu.lp2_gp2.dto.SalidaProgramadaDTO;
import pe.edu.upeu.lp2_gp2.dto.SalidaProgramadaRequestDTO;

import java.util.List;

public interface ISalidaProgramadaService {
    List<SalidaProgramadaDTO> listarTodo();
    SalidaProgramadaDTO buscarPorId(Long id);
    SalidaProgramadaDTO crear(SalidaProgramadaRequestDTO dto);
    SalidaProgramadaDTO actualizar(Long id, SalidaProgramadaRequestDTO dto);
    void eliminar(Long id);
}
