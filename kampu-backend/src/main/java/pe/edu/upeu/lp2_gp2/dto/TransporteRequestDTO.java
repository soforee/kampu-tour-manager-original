package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.*;

public record TransporteRequestDTO(
        @NotBlank(message = "La placa es obligatoria.")
        @Size(max = 15, message = "La placa admite máximo 15 caracteres.")
        String placa,
        @Size(max = 100, message = "La marca/modelo admite máximo 100 caracteres.")
        String marcaModelo,
        @NotNull(message = "La capacidad es obligatoria.")
        @Min(value = 1, message = "La capacidad debe estar entre 1 y 999.")
        @Max(value = 999, message = "La capacidad debe estar entre 1 y 999.")
        Integer capacidad
) {
}
