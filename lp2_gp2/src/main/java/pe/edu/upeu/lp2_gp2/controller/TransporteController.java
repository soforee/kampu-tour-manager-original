package pe.edu.upeu.lp2_gp2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.lp2_gp2.dto.TransporteDTO;
import pe.edu.upeu.lp2_gp2.dto.TransporteRequestDTO;
import pe.edu.upeu.lp2_gp2.services.ITransporteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transportes")
public class TransporteController {
    private final ITransporteService transporteService;

    public TransporteController(ITransporteService transporteService) {
        this.transporteService = transporteService;
    }

    @GetMapping
    public ResponseEntity<List<TransporteDTO>> listar() {
        return ResponseEntity.ok(transporteService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransporteDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(transporteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TransporteDTO> crear(@Valid @RequestBody TransporteRequestDTO dto) {
        return new ResponseEntity<>(transporteService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransporteDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TransporteRequestDTO dto) {
        return ResponseEntity.ok(transporteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
