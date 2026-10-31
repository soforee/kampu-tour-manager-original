package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PasajeroRequestDTO(
        @NotBlank(message = "Completa nombre y documento de todos los pasajeros.")
        @Size(max = 150, message = "El nombre del pasajero admite máximo 150 caracteres.")
        String nombreCompleto,
        @NotNull(message = "El tipo de documento del pasajero es obligatorio.")
        @Pattern(regexp = "DNI|CE|PASAPORTE", message = "El tipo de documento debe ser DNI, CE o PASAPORTE.")
        String tipoDocumento,
        @NotBlank(message = "Completa nombre y documento de todos los pasajeros.")
        @Size(max = 20, message = "El documento del pasajero admite máximo 20 caracteres.")
        String numeroDocumento,
        @Size(max = 50, message = "La nacionalidad admite máximo 50 caracteres.")
        String nacionalidad,
        LocalDate fechaNacimiento
) {
}
