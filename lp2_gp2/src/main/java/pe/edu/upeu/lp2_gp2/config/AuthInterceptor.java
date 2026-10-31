package pe.edu.upeu.lp2_gp2.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.edu.upeu.lp2_gp2.exception.UnauthorizedException;
import pe.edu.upeu.lp2_gp2.services.IAuthService;

/**
 * Exige "Authorization: Bearer <token>" en /api/**, salvo el login.
 * Deja el id del usuario en el atributo "usuarioId" (lo usa la anulación de reservas).
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String USUARIO_ID = "usuarioId";

    private final IAuthService authService;

    public AuthInterceptor(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // preflight CORS
        String header = request.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7).trim() : null;
        Long usuarioId = authService.usuarioDeToken(token)
                .orElseThrow(() -> new UnauthorizedException("Tu sesión expiró. Vuelve a iniciar sesión."));
        request.setAttribute(USUARIO_ID, usuarioId);
        return true;
    }
}
