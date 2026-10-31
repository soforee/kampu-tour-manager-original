package pe.edu.upeu.lp2_gp2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Propiedades "tour.*" de application.yaml.
 *
 * @param tasas       tipo de cambio a PEN (1 unidad -> PEN). Deben coincidir con TASAS de core/utils/moneda.ts del frontend.
 * @param sesionHoras horas de vigencia del token de sesión.
 */
@ConfigurationProperties(prefix = "tour")
public record TourProperties(Map<String, BigDecimal> tasas, Integer sesionHoras) {
    public TourProperties {
        if (tasas == null || tasas.isEmpty()) {
            tasas = Map.of("PEN", BigDecimal.ONE, "USD", new BigDecimal("3.75"), "EUR", new BigDecimal("4.05"));
        }
        if (sesionHoras == null || sesionHoras < 1) sesionHoras = 12;
    }
}
