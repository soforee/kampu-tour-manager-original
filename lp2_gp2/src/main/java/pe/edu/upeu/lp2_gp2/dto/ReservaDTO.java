package pe.edu.upeu.lp2_gp2.dto;

import pe.edu.upeu.lp2_gp2.entity.Reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static pe.edu.upeu.lp2_gp2.util.Texto.nz;

public record ReservaDTO(
        Long reservaId,
        Long clienteId,
        Long salidaId,
        LocalDateTime fechaReserva,
        Integer numeroViajeros,
        BigDecimal montoTotal,
        BigDecimal montoPagado,
        String estadoReserva,
        String estadoPago,
        String observaciones,
        String formaReembolso,
        Long usuarioAnulaId
) {
    public static ReservaDTO desde(Reserva r) {
        return new ReservaDTO(r.getReservaId(), r.getClienteId(), r.getSalidaId(), r.getFechaReserva(),
                r.getNumeroViajeros(), r.getMontoTotal(), r.getMontoPagado() == null ? BigDecimal.ZERO : r.getMontoPagado(),
                r.getEstadoReserva(), r.getEstadoPago(), nz(r.getObservaciones()), r.getFormaReembolso(), r.getUsuarioAnulaId());
    }
}
