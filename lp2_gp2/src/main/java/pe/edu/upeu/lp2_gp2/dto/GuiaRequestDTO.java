package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuiaRequestDTO(
        @NotBlank(message = "El nombre completo es obligatorio.")
        @Size(max = 150, message = "El nombre admite máximo 150 caracteres.")
        String nombreCompleto,
        @Size(max = 100, message = "La especialidad admite máximo 100 caracteres.")
        String especialidad,
        @Size(max = 20, message = "El teléfono admite máximo 20 caracteres.")
        String telefono
) {
}
