package pe.edu.upeu.lp2_gp2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KAMPU Tour Manager - API")
                        .version("1.0")
                        .description("API REST del sistema de reservas de tours. Inicia sesión en POST /api/v1/auth/login "
                                + "y usa el token en el botón Authorize."))
                // botón "Authorize" de Swagger para enviar Authorization: Bearer <token>
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
