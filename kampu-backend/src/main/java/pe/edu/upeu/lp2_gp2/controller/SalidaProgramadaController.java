package pe.edu.upeu.lp2_gp2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.lp2_gp2.dto.SalidaProgramadaDTO;
import pe.edu.upeu.lp2_gp2.dto.SalidaProgramadaRequestDTO;
import pe.edu.upeu.lp2_gp2.services.ISalidaProgramadaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salidas")
public class SalidaProgramadaController {
    private final ISalidaProgramadaService salidaService;

    public SalidaProgramadaController(ISalidaProgramadaService salidaService) {
        this.salidaService = salidaService;
    }

    @GetMapping
    public ResponseEntity<List<SalidaProgramadaDTO>> listar() {
        return ResponseEntity.ok(salidaService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalidaProgramadaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(salidaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SalidaProgramadaDTO> crear(@Valid @RequestBody SalidaProgramadaRequestDTO dto) {
        return new ResponseEntity<>(salidaService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalidaProgramadaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody SalidaProgramadaRequestDTO dto) {
        return ResponseEntity.ok(salidaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        salidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
