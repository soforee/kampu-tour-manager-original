package pe.edu.upeu.lp2_gp2.exception;

/** Credenciales inválidas o sesión vencida. Responde 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
