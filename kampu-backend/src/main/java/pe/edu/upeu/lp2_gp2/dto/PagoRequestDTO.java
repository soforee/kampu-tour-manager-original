package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PagoRequestDTO(
        @NotNull(message = "Reserva no encontrada.")
        Long reservaId,
        @NotNull(message = "El monto debe ser mayor a 0.")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.")
        @Digits(integer = 8, fraction = 2, message = "El monto admite 8 enteros y 2 decimales.")
        BigDecimal monto,
        @NotNull(message = "La moneda es obligatoria.")
        @Pattern(regexp = "PEN|USD|EUR", message = "La moneda debe ser PEN, USD o EUR.")
        String moneda,
        @Size(max = 50, message = "El método de pago admite máximo 50 caracteres.")
        String metodoPago,
        @Size(max = 100, message = "El código de transacción admite máximo 100 caracteres.")
        String codigoTransaccion,
        @NotNull(message = "El estado de la transacción es obligatorio.")
        @Pattern(regexp = "PENDIENTE|APROBADO|RECHAZADO", message = "Un pago nuevo solo puede ser PENDIENTE, APROBADO o RECHAZADO.")
        String estadoTransaccion
) {
}
