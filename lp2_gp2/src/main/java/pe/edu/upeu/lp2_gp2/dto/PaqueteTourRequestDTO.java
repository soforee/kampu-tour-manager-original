package pe.edu.upeu.lp2_gp2.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaqueteTourRequestDTO(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre admite máximo 100 caracteres.")
        String nombre,
        @Size(max = 1000, message = "La descripción admite máximo 1000 caracteres.")
        String descripcion,
        @NotNull(message = "El precio base es obligatorio.")
        @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a 0.")
        @Digits(integer = 8, fraction = 2, message = "El precio base admite 8 enteros y 2 decimales.")
        BigDecimal precioBase,
        @NotNull(message = "Los días son obligatorios.")
        @Min(value = 1, message = "Los días deben estar entre 1 y 99.")
        @Max(value = 99, message = "Los días deben estar entre 1 y 99.")
        Integer dias,
        @NotNull(message = "Las noches son obligatorias.")
        @Min(value = 0, message = "Las noches deben estar entre 0 y 99.")
        @Max(value = 99, message = "Las noches deben estar entre 0 y 99.")
        Integer noches,
        @Size(max = 500, message = "La ruta de la imagen admite máximo 500 caracteres.")
        String imagen,
        @Pattern(regexp = "ACTIVO|INACTIVO", message = "El estado debe ser ACTIVO o INACTIVO.")
        String estado
) {
}
