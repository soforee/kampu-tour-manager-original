import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Resultado } from '../models';
import { API, IMAGENES, mensajeError } from '../utils/api';

const MAX_ORIGINAL_MB = 10;

/**
 * Imágenes subidas desde el equipo. PAQUETES_TOUR.imagen guarda solo una ruta corta
 * (ej. "paquetes/<uuid>.jpg", máx. 500 caracteres); el archivo lo guarda el backend en su carpeta
 * storage.location y lo sirve en /lp2_gp2/images/<ruta>. Al cambiar o quitar la imagen de un paquete,
 * el backend borra el archivo anterior.
 */
@Injectable({ providedIn: 'root' })
export class ImagenService {
  private http = inject(HttpClient);

  /** URL pública de una ruta guardada, o null si no hay imagen. */
  url(ref?: string | null): string | null {
    return ref ? `${IMAGENES}/${ref}` : null;
  }

  /** Redimensiona (máx. 1280 px de ancho) y comprime a JPEG. Devuelve un data URL. */
  async comprimir(file: File, maxAncho = 1280, calidad = 0.82): Promise<string> {
    if (!file.type.startsWith('image/')) throw new Error('El archivo debe ser una imagen (JPG, PNG o WebP).');
    if (file.size > MAX_ORIGINAL_MB * 1024 * 1024) throw new Error(`La imagen supera los ${MAX_ORIGINAL_MB} MB.`);

    const origen = URL.createObjectURL(file);
    try {
      const img = await new Promise<HTMLImageElement>((ok, err) => {
        const i = new Image();
        i.onload = () => ok(i);
        i.onerror = () => err(new Error('No se pudo leer la imagen.'));
        i.src = origen;
      });
      const escala = Math.min(1, maxAncho / img.naturalWidth);
      const canvas = document.createElement('canvas');
      canvas.width = Math.round(img.naturalWidth * escala);
      canvas.height = Math.round(img.naturalHeight * escala);
      const ctx = canvas.getContext('2d')!;
      ctx.fillStyle = '#F4F1E8'; // fondo crema para PNG con transparencia
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
      return canvas.toDataURL('image/jpeg', calidad);
    } finally {
      URL.revokeObjectURL(origen);
    }
  }

  /** Sube la imagen al backend y devuelve la ruta que se almacena en la columna IMAGEN. */
  async guardar(dataUrl: string): Promise<Resultado<string>> {
    try {
      const blob = await (await fetch(dataUrl)).blob();
      const datos = new FormData();
      datos.append('file', blob, 'imagen.jpg');
      const r = await firstValueFrom(this.http.post<{ imagen: string }>(`${API}/paquetes/imagen`, datos));
      return { ok: true, data: r.imagen };
    } catch (e) {
      return { ok: false, error: mensajeError(e) };
    }
  }
}
