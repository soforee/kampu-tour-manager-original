import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

const BRAND = '#16301F';
const DANGER = '#e11d48';

@Injectable({ providedIn: 'root' })
export class AlertService {
  success(message: string, title: string = '¡Listo!') {
    Swal.fire({ title, text: message, icon: 'success', confirmButtonColor: BRAND });
  }

  error(message: string, title: string = 'No se pudo completar') {
    Swal.fire({ title, text: message, icon: 'error', confirmButtonColor: DANGER });
  }

  async confirm(message: string, title: string = '¿Estás seguro?', confirmText: string = 'Sí, continuar'): Promise<boolean> {
    const result = await Swal.fire({
      title, text: message, icon: 'warning', showCancelButton: true,
      confirmButtonColor: BRAND, cancelButtonColor: '#76705A',
      confirmButtonText: confirmText, cancelButtonText: 'Cancelar'
    });
    return result.isConfirmed;
  }

  /** Lista desplegable; devuelve la opción elegida o null si se cancela */
  async select(title: string, text: string, options: string[]): Promise<string | null> {
    const inputOptions: Record<string, string> = {};
    options.forEach(o => (inputOptions[o] = o));
    const result = await Swal.fire({
      title, text, input: 'select', inputOptions, inputPlaceholder: 'Selecciona una opción',
      showCancelButton: true, confirmButtonColor: DANGER, cancelButtonColor: '#76705A',
      confirmButtonText: 'Anular reserva', cancelButtonText: 'Volver',
      inputValidator: v => (v ? null : 'Elige una opción')
    });
    return result.isConfirmed ? (result.value as string) : null;
  }

  toast(message: string, icon: 'success' | 'error' | 'info' = 'success') {
    Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 2500, timerProgressBar: true })
      .fire({ icon, title: message });
  }
}
