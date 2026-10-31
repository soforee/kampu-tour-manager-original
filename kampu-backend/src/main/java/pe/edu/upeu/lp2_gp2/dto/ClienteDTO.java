package pe.edu.upeu.lp2_gp2.dto;

import pe.edu.upeu.lp2_gp2.entity.Cliente;

import static pe.edu.upeu.lp2_gp2.util.Texto.nz;

public record ClienteDTO(
        Long clienteId,
        String nombreCompleto,
        String tipoDocumento,
        String numeroDocumento,
        String email,
        String telefono
) {
    public static ClienteDTO desde(Cliente c) {
        return new ClienteDTO(c.getClienteId(), c.getNombreCompleto(), c.getTipoDocumento(), c.getNumeroDocumento(),
                nz(c.getEmail()), nz(c.getTelefono()));
    }
}
