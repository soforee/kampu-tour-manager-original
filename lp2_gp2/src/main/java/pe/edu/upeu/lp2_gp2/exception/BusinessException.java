package pe.edu.upeu.lp2_gp2.exception;

/** Regla de negocio incumplida (cupos, estados, saldos, fechas...). Responde 400. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
