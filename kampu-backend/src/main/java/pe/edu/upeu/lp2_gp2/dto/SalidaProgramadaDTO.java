package pe.edu.upeu.lp2_gp2.dto;

import pe.edu.upeu.lp2_gp2.entity.SalidaProgramada;

import java.time.LocalDate;

public record SalidaProgramadaDTO(
        Long salidaId,
        Long paqueteId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer cuposMaximos,
        Integer cuposDisponibles,
        Long guiaId,
        Long transporteId,
        String estado
) {
    public static SalidaProgramadaDTO desde(SalidaProgramada s) {
        return new SalidaProgramadaDTO(s.getSalidaId(), s.getPaqueteId(), s.getFechaInicio(), s.getFechaFin(),
                s.getCuposMaximos(), s.getCuposDisponibles(), s.getGuiaId(), s.getTransporteId(), s.getEstado());
    }
}
