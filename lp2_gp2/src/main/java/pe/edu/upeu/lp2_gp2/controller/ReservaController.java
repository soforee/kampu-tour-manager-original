package pe.edu.upeu.lp2_gp2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.lp2_gp2.config.AuthInterceptor;
import pe.edu.upeu.lp2_gp2.dto.AnulacionRequestDTO;
import pe.edu.upeu.lp2_gp2.dto.PasajeroReservaDTO;
import pe.edu.upeu.lp2_gp2.dto.ReservaDTO;
import pe.edu.upeu.lp2_gp2.dto.ReservaRequestDTO;
import pe.edu.upeu.lp2_gp2.services.IReservaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {
    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar() {
        return ResponseEntity.ok(reservaService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping("/{id}/pasajeros")
    public ResponseEntity<List<PasajeroReservaDTO>> pasajeros(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPasajeros(id));
    }

    /** Crea la reserva con sus pasajeros y descuenta cupos de la salida. */
    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        return new ResponseEntity<>(reservaService.crear(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<ReservaDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.completar(id));
    }

    /** Anula: libera cupos, reembolsa pagos aprobados y guarda el usuario de la sesión en usuario_anula_id. */
    @PatchMapping("/{id}/anular")
    public ResponseEntity<ReservaDTO> anular(@PathVariable Long id,
                                             @Valid @RequestBody(required = false) AnulacionRequestDTO dto,
                                             @RequestAttribute(AuthInterceptor.USUARIO_ID) Long usuarioId) {
        return ResponseEntity.ok(reservaService.anular(id, usuarioId, dto == null ? null : dto.formaReembolso()));
    }
}
