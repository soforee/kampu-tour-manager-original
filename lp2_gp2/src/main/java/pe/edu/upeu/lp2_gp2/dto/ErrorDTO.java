package pe.edu.upeu.lp2_gp2.dto;

import java.time.LocalDateTime;

/** Cuerpo de error; el frontend muestra el campo "error". */
public record ErrorDTO(
        int status,
        String error,
        LocalDateTime timestamp
) {
    public ErrorDTO(int status, String error) {
        this(status, error, LocalDateTime.now());
    }
}
