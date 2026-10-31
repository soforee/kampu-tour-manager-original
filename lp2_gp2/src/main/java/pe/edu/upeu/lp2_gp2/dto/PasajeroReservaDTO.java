package pe.edu.upeu.lp2_gp2.dto;

import pe.edu.upeu.lp2_gp2.entity.PasajeroReserva;

import java.time.LocalDate;

import static pe.edu.upeu.lp2_gp2.util.Texto.nz;

public record PasajeroReservaDTO(
        Long pasajeroId,
        Long reservaId,
        String nombreCompleto,
        String tipoDocumento,
        String numeroDocumento,
        String nacionalidad,
        LocalDate fechaNacimiento
) {
    public static PasajeroReservaDTO desde(PasajeroReserva p) {
        return new PasajeroReservaDTO(p.getPasajeroId(), p.getReservaId(), p.getNombreCompleto(), p.getTipoDocumento(),
                p.getNumeroDocumento(), nz(p.getNacionalidad()), p.getFechaNacimiento());
    }
}
