package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReservaRequestDTO(
        @NotNull(message = "Selecciona un cliente.")
        Long clienteId,
        @NotNull(message = "Selecciona una salida.")
        Long salidaId,
        @Size(max = 200, message = "Las observaciones admiten máximo 200 caracteres.")
        String observaciones,
        @NotEmpty(message = "Agrega al menos un pasajero.")
        @Size(max = 99, message = "Máximo 99 viajeros por reserva.")
        List<@Valid PasajeroRequestDTO> pasajeros
) {
}
