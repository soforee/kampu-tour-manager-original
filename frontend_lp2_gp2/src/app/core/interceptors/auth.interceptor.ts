import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { API } from '../utils/api';

/** Envía el token en cada llamada a la API; si el backend responde 401, vuelve al login. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token();
  const conToken = token && req.url.startsWith(API) ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(conToken).pipe(
    catchError((e: unknown) => {
      if (e instanceof HttpErrorResponse && e.status === 401 && !req.url.endsWith('/auth/login')) {
        auth.cerrarSesionLocal();
        router.navigate(['/login']);
      }
      return throwError(() => e);
    })
  );
};
