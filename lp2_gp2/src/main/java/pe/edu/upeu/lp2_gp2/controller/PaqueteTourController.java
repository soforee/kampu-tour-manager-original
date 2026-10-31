package pe.edu.upeu.lp2_gp2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.lp2_gp2.dto.ImagenDTO;
import pe.edu.upeu.lp2_gp2.dto.PaqueteTourDTO;
import pe.edu.upeu.lp2_gp2.dto.PaqueteTourRequestDTO;
import pe.edu.upeu.lp2_gp2.services.IPaqueteTourService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paquetes")
public class PaqueteTourController {
    private final IPaqueteTourService paqueteService;

    public PaqueteTourController(IPaqueteTourService paqueteService) {
        this.paqueteService = paqueteService;
    }

    @GetMapping
    public ResponseEntity<List<PaqueteTourDTO>> listar() {
        return ResponseEntity.ok(paqueteService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaqueteTourDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(paqueteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PaqueteTourDTO> crear(@Valid @RequestBody PaqueteTourRequestDTO dto) {
        return new ResponseEntity<>(paqueteService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaqueteTourDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PaqueteTourRequestDTO dto) {
        return ResponseEntity.ok(paqueteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        paqueteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Sube la imagen y devuelve la ruta que se guarda en PAQUETES_TOUR.imagen (servida en /lp2_gp2/images/<ruta>). */
    @PostMapping(value = "/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagenDTO> subirImagen(@RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(new ImagenDTO(paqueteService.subirImagen(file)), HttpStatus.CREATED);
    }
}
