package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.*;

public record ClienteRequestDTO(
        @NotBlank(message = "Nombre y documento son obligatorios.")
        @Size(max = 150, message = "El nombre admite máximo 150 caracteres.")
        String nombreCompleto,
        @NotNull(message = "El tipo de documento es obligatorio.")
        @Pattern(regexp = "DNI|CE|PASAPORTE", message = "El tipo de documento debe ser DNI, CE o PASAPORTE.")
        String tipoDocumento,
        @NotBlank(message = "Nombre y documento son obligatorios.")
        @Size(max = 20, message = "El documento admite máximo 20 caracteres.")
        String numeroDocumento,
        @Email(message = "El email no es válido.")
        @Size(max = 100, message = "El email admite máximo 100 caracteres.")
        String email,
        @Size(max = 20, message = "El teléfono admite máximo 20 caracteres.")
        String telefono
) {
}
