import { HttpErrorResponse } from '@angular/common/http';

/** Base de la API. En desarrollo, proxy.config.json la redirige a http://localhost:8082/lp2_gp2/api */
export const API = '/api/v1';

/** Las imágenes de PAQUETES_TOUR.imagen se sirven desde el backend (/lp2_gp2/images/<ruta>). */
export const IMAGENES = '/lp2_gp2/images';

/** Extrae el mensaje que envía el backend ({ status, error, timestamp }). */
export function mensajeError(e: unknown): string {
  if (e instanceof HttpErrorResponse) {
    if (e.status === 0) return 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.';
    const cuerpo = e.error;
    if (cuerpo && typeof cuerpo === 'object' && typeof cuerpo.error === 'string') return cuerpo.error;
    return `El servidor respondió con un error (${e.status}).`;
  }
  return e instanceof Error ? e.message : 'Ocurrió un error inesperado.';
}
