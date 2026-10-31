package pe.edu.upeu.lp2_gp2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.lp2_gp2.dto.PagoDTO;
import pe.edu.upeu.lp2_gp2.dto.PagoRequestDTO;
import pe.edu.upeu.lp2_gp2.dto.ResolucionPagoRequestDTO;
import pe.edu.upeu.lp2_gp2.services.IPagoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    private final IPagoService pagoService;

    public PagoController(IPagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<PagoDTO>> listar(@RequestParam(required = false) Long reservaId) {
        return ResponseEntity.ok(pagoService.listar(reservaId));
    }

    /** Registra el pago y recalcula monto_pagado / estado_pago de la reserva. */
    @PostMapping
    public ResponseEntity<PagoDTO> registrar(@Valid @RequestBody PagoRequestDTO dto) {
        return new ResponseEntity<>(pagoService.registrar(dto), HttpStatus.CREATED);
    }

    /** Aprueba o rechaza un pago PENDIENTE. */
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<PagoDTO> resolver(@PathVariable Long id, @Valid @RequestBody ResolucionPagoRequestDTO dto) {
        return ResponseEntity.ok(pagoService.resolver(id, dto.estado()));
    }
}
