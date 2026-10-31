package pe.edu.upeu.lp2_gp2.services;

import org.springframework.stereotype.Component;
import pe.edu.upeu.lp2_gp2.config.TourProperties;
import pe.edu.upeu.lp2_gp2.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Conversión a PEN con las tasas de "tour.tasas" (mismas que core/utils/moneda.ts del frontend). */
@Component
public class ConversorMoneda {
    /** Tolerancia de redondeo al comparar contra el saldo (igual que el frontend). */
    public static final BigDecimal TOLERANCIA = new BigDecimal("0.005");

    private final TourProperties props;

    public ConversorMoneda(TourProperties props) {
        this.props = props;
    }

    public BigDecimal aPEN(BigDecimal monto, String moneda) {
        BigDecimal tasa = props.tasas().get(moneda);
        if (tasa == null) throw new BusinessException("Moneda no soportada: " + moneda);
        return monto.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
    }
}
