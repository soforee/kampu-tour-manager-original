package pe.edu.upeu.lp2_gp2.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.lp2_gp2.dto.PaqueteTourDTO;
import pe.edu.upeu.lp2_gp2.dto.PaqueteTourRequestDTO;
import pe.edu.upeu.lp2_gp2.entity.PaqueteTour;
import pe.edu.upeu.lp2_gp2.exception.BusinessException;
import pe.edu.upeu.lp2_gp2.exception.ResourceNotFoundException;
import pe.edu.upeu.lp2_gp2.repository.PaqueteTourRepository;
import pe.edu.upeu.lp2_gp2.repository.SalidaProgramadaRepository;

import java.util.List;
import java.util.Objects;

import static pe.edu.upeu.lp2_gp2.util.Texto.limpio;

@Service
public class PaqueteTourServiceImpl implements IPaqueteTourService {
    private final PaqueteTourRepository paqueteRepository;
    private final SalidaProgramadaRepository salidaRepository;
    private final IFileStorageService fileStorageService;

    public PaqueteTourServiceImpl(PaqueteTourRepository paqueteRepository, SalidaProgramadaRepository salidaRepository,
                                  IFileStorageService fileStorageService) {
        this.paqueteRepository = paqueteRepository;
        this.salidaRepository = salidaRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteTourDTO> listarTodo() {
        return paqueteRepository.findAll(Sort.by("paqueteId")).stream().map(PaqueteTourDTO::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaqueteTourDTO buscarPorId(Long id) {
        return PaqueteTourDTO.desde(obtener(id));
    }

    @Override
    @Transactional
    public PaqueteTourDTO crear(PaqueteTourRequestDTO dto) {
        PaqueteTour p = new PaqueteTour();
        copiar(dto, p);
        return PaqueteTourDTO.desde(paqueteRepository.save(p));
    }

    @Override
    @Transactional
    public PaqueteTourDTO actualizar(Long id, PaqueteTourRequestDTO dto) {
        PaqueteTour p = obtener(id);
        String imagenAnterior = p.getImagen();
        copiar(dto, p);
        PaqueteTourDTO guardado = PaqueteTourDTO.desde(paqueteRepository.save(p));
        // Si la imagen cambió o se quitó, se borra el archivo anterior para no dejar basura
        if (imagenAnterior != null && !Objects.equals(imagenAnterior, p.getImagen())) {
            fileStorageService.delete(imagenAnterior);
        }
        return guardado;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        PaqueteTour p = obtener(id);
        if (salidaRepository.existsByPaqueteId(id)) {
            throw new BusinessException("El paquete tiene salidas programadas. Desactívalo en lugar de eliminarlo.");
        }
        paqueteRepository.delete(p);
        fileStorageService.delete(p.getImagen());
    }

    @Override
    public String subirImagen(MultipartFile file) {
        return fileStorageService.store(file, "paquetes");
    }

    private PaqueteTour obtener(Long id) {
        return paqueteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete no encontrado con Id: " + id));
    }

    private void copiar(PaqueteTourRequestDTO dto, PaqueteTour p) {
        p.setNombre(dto.nombre().trim());
        p.setDescripcion(limpio(dto.descripcion()));
        p.setPrecioBase(dto.precioBase());
        p.setDias(dto.dias());
        p.setNoches(dto.noches());
        p.setImagen(limpio(dto.imagen()));
        p.setEstado(dto.estado() == null ? "ACTIVO" : dto.estado());
    }
}
