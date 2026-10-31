package pe.edu.upeu.lp2_gp2.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.lp2_gp2.dto.UsuarioDTO;
import pe.edu.upeu.lp2_gp2.repository.UsuarioSistemaRepository;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioSistemaRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioSistemaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodo() {
        return usuarioRepository.findAll(Sort.by("usuarioId")).stream().map(UsuarioDTO::desde).toList();
    }
}
