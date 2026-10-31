package pe.edu.upeu.lp2_gp2.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.lp2_gp2.dto.PagoDTO;
import pe.edu.upeu.lp2_gp2.dto.PagoRequestDTO;
import pe.edu.upeu.lp2_gp2.entity.Pago;
import pe.edu.upeu.lp2_gp2.entity.Reserva;
import pe.edu.upeu.lp2_gp2.exception.BusinessException;
import pe.edu.upeu.lp2_gp2.exception.ResourceNotFoundException;
import pe.edu.upeu.lp2_gp2.repository.PagoRepository;
import pe.edu.upeu.lp2_gp2.repository.ReservaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static pe.edu.upeu.lp2_gp2.util.Texto.limpio;

/**
 * Reglas de PAGOS: un pago APROBADO no puede exceder el saldo; RESERVAS.monto_pagado es la suma
 * de pagos APROBADOS convertidos a PEN y estado_pago se deriva (PENDIENTE / PARCIAL / PAGADO).
 */
@Service
public class PagoServiceImpl implements IPagoService {
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final ConversorMoneda conversor;

    public PagoServiceImpl(PagoRepository pagoRepository, ReservaRepository reservaRepository, ConversorMoneda conversor) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.conversor = conversor;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoDTO> listar(Long reservaId) {
        List<Pago> lista = reservaId == null
                ? pagoRepository.findAll(Sort.by("pagoId"))
                : pagoRepository.findByReservaIdOrderByPagoId(reservaId);
        return lista.stream().map(PagoDTO::desde).toList();
    }

    @Override
    @Transactional
    public PagoDTO registrar(PagoRequestDTO dto) {
        Reserva r = reservaRepository.findByIdParaActualizar(dto.reservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada."));
        if (!"PENDIENTE".equals(r.getEstadoReserva()) && !"CONFIRMADA".equals(r.getEstadoReserva())) {
            throw new BusinessException("La reserva no admite pagos.");
        }
        if ("APROBADO".equals(dto.estadoTransaccion())
                && conversor.aPEN(dto.monto(), dto.moneda()).compareTo(saldo(r).add(ConversorMoneda.TOLERANCIA)) > 0) {
            throw new BusinessException("El pago excede el saldo pendiente (S/ " + saldo(r).setScale(2) + ").");
        }
        Pago p = new Pago();
        p.setReservaId(dto.reservaId());
        p.setMonto(dto.monto().setScale(2, java.math.RoundingMode.HALF_UP));
        p.setMoneda(dto.moneda());
        p.setMetodoPago(limpio(dto.metodoPago()));
        String codigo = limpio(dto.codigoTransaccion());
        if (codigo == null) {
            String millis = String.valueOf(System.currentTimeMillis());
            codigo = "TRX-" + millis.substring(millis.length() - 8);
        }
        p.setCodigoTransaccion(codigo);
        p.setFechaPago(LocalDateTime.now());
        p.setEstadoTransaccion(dto.estadoTransaccion());
        Pago guardado = pagoRepository.save(p);
        recalcular(r);
        return PagoDTO.desde(guardado);
    }

    @Override
    @Transactional
    public PagoDTO resolver(Long pagoId, String estado) {
        Pago p = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con Id: " + pagoId));
        if (!"PENDIENTE".equals(p.getEstadoTransaccion())) throw new BusinessException("Solo se resuelven pagos pendientes.");
        Reserva r = reservaRepository.findByIdParaActualizar(p.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada."));
        if ("ANULADA".equals(r.getEstadoReserva())) throw new BusinessException("La reserva está anulada.");
        if ("APROBADO".equals(estado)
                && conversor.aPEN(p.getMonto(), p.getMoneda()).compareTo(saldo(r).add(ConversorMoneda.TOLERANCIA)) > 0) {
            throw new BusinessException("Aprobar este pago excedería el saldo de la reserva.");
        }
        p.setEstadoTransaccion(estado);
        Pago guardado = pagoRepository.save(p);
        recalcular(r);
        return PagoDTO.desde(guardado);
    }

    private BigDecimal saldo(Reserva r) {
        if ("ANULADA".equals(r.getEstadoReserva())) return BigDecimal.ZERO;
        BigDecimal pagado = r.getMontoPagado() == null ? BigDecimal.ZERO : r.getMontoPagado();
        return r.getMontoTotal().subtract(pagado).max(BigDecimal.ZERO);
    }

    /** monto_pagado = suma de pagos APROBADOS en PEN (tope monto_total, por chk_res_monto_pagado). */
    private void recalcular(Reserva r) {
        if ("ANULADA".equals(r.getEstadoReserva())) return;
        BigDecimal suma = pagoRepository.findByReservaIdAndEstadoTransaccion(r.getReservaId(), "APROBADO").stream()
                .map(p -> conversor.aPEN(p.getMonto(), p.getMoneda()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pagado = suma.min(r.getMontoTotal());
        String estadoPago;
        if (pagado.signum() <= 0) estadoPago = "PENDIENTE";
        else if (pagado.compareTo(r.getMontoTotal().subtract(ConversorMoneda.TOLERANCIA)) >= 0) estadoPago = "PAGADO";
        else estadoPago = "PARCIAL";
        r.setMontoPagado(pagado);
        r.setEstadoPago(estadoPago);
        reservaRepository.save(r);
    }
}
