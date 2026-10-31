package pe.edu.upeu.lp2_gp2.exception;

/** Usuario autenticado pero sin permiso (ej. usuario INACTIVO). Responde 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
