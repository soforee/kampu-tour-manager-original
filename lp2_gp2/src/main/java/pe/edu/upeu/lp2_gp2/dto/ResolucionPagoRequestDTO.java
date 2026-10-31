package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ResolucionPagoRequestDTO(
        @NotNull(message = "Indica si el pago se aprueba o se rechaza.")
        @Pattern(regexp = "APROBADO|RECHAZADO", message = "Solo se puede APROBAR o RECHAZAR un pago pendiente.")
        String estado
) {
}
