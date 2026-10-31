package pe.edu.upeu.lp2_gp2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.lp2_gp2.dto.GuiaDTO;
import pe.edu.upeu.lp2_gp2.dto.GuiaRequestDTO;
import pe.edu.upeu.lp2_gp2.services.IGuiaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/guias")
public class GuiaController {
    private final IGuiaService guiaService;

    public GuiaController(IGuiaService guiaService) {
        this.guiaService = guiaService;
    }

    @GetMapping
    public ResponseEntity<List<GuiaDTO>> listar() {
        return ResponseEntity.ok(guiaService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(guiaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<GuiaDTO> crear(@Valid @RequestBody GuiaRequestDTO dto) {
        return new ResponseEntity<>(guiaService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody GuiaRequestDTO dto) {
        return ResponseEntity.ok(guiaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        guiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
