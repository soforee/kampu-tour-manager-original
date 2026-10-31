package pe.edu.upeu.lp2_gp2.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.lp2_gp2.dto.GuiaDTO;
import pe.edu.upeu.lp2_gp2.dto.GuiaRequestDTO;
import pe.edu.upeu.lp2_gp2.entity.Guia;
import pe.edu.upeu.lp2_gp2.exception.BusinessException;
import pe.edu.upeu.lp2_gp2.exception.ResourceNotFoundException;
import pe.edu.upeu.lp2_gp2.repository.GuiaRepository;
import pe.edu.upeu.lp2_gp2.repository.SalidaProgramadaRepository;

import java.util.List;

import static pe.edu.upeu.lp2_gp2.util.Texto.limpio;

@Service
public class GuiaServiceImpl implements IGuiaService {
    private final GuiaRepository guiaRepository;
    private final SalidaProgramadaRepository salidaRepository;

    public GuiaServiceImpl(GuiaRepository guiaRepository, SalidaProgramadaRepository salidaRepository) {
        this.guiaRepository = guiaRepository;
        this.salidaRepository = salidaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuiaDTO> listarTodo() {
        return guiaRepository.findAll(Sort.by("guiaId")).stream().map(GuiaDTO::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GuiaDTO buscarPorId(Long id) {
        return GuiaDTO.desde(obtener(id));
    }

    @Override
    @Transactional
    public GuiaDTO crear(GuiaRequestDTO dto) {
        Guia g = new Guia();
        copiar(dto, g);
        return GuiaDTO.desde(guiaRepository.save(g));
    }

    @Override
    @Transactional
    public GuiaDTO actualizar(Long id, GuiaRequestDTO dto) {
        Guia g = obtener(id);
        copiar(dto, g);
        return GuiaDTO.desde(guiaRepository.save(g));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Guia g = obtener(id);
        if (salidaRepository.existsByGuiaId(id)) throw new BusinessException("El guía está asignado a salidas programadas.");
        guiaRepository.delete(g);
    }

    private Guia obtener(Long id) {
        return guiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guía no encontrado con Id: " + id));
    }

    private void copiar(GuiaRequestDTO dto, Guia g) {
        g.setNombreCompleto(dto.nombreCompleto().trim());
        g.setEspecialidad(limpio(dto.especialidad()));
        g.setTelefono(limpio(dto.telefono()));
    }
}
