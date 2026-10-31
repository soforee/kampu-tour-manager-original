import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UsuarioSistema } from '../models';
import { API, mensajeError } from '../utils/api';

const KEY = 'tm3_session';

export type Sesion = Omit<UsuarioSistema, 'passwordHash'>;
interface SesionGuardada { token: string; usuario: Sesion; }

/** Autenticación contra el backend (USUARIOS_SISTEMA: username + password_hash + estado ACTIVO). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private _sesion = signal<SesionGuardada | null>(this.cargar());
  usuario = computed(() => this._sesion()?.usuario ?? null);
  autenticado = computed(() => this._sesion() !== null);

  private cargar(): SesionGuardada | null {
    try {
      const raw = localStorage.getItem(KEY);
      const s = raw ? JSON.parse(raw) : null;
      return s?.token && s?.usuario ? s : null; // ignora sesiones de la versión sin backend
    } catch { return null; }
  }

  token(): string | null {
    return this._sesion()?.token ?? null;
  }

  /** Devuelve null si ok; o el mensaje de error. */
  async login(username: string, password: string): Promise<string | null> {
    try {
      const s = await firstValueFrom(this.http.post<SesionGuardada>(`${API}/auth/login`, { username: username.trim(), password }));
      this._sesion.set(s);
      localStorage.setItem(KEY, JSON.stringify(s));
      return null;
    } catch (e) {
      return mensajeError(e);
    }
  }

  logout() {
    if (this.token()) this.http.post(`${API}/auth/logout`, {}).subscribe({ error: () => { /* la sesión local se cierra igual */ } });
    this.cerrarSesionLocal();
  }

  /** Olvida la sesión sin llamar al backend (ej. token vencido). */
  cerrarSesionLocal() {
    this._sesion.set(null);
    localStorage.removeItem(KEY);
  }
}
