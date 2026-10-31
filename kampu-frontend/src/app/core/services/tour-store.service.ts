import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { firstValueFrom, Observable } from 'rxjs';
import {
  Cliente, EstadoTransaccion, Guia, Moneda, PaqueteTour, Pago, PasajeroDraft, PasajeroReserva,
  Reserva, Resultado, SalidaForm, SalidaProgramada, Transporte, UsuarioSistema
} from '../models';
import { redondear } from '../utils/moneda';
import { API, mensajeError } from '../utils/api';

const ok = <T>(data?: T): Resultado<T> => ({ ok: true, data });
const fail = (error: string): Resultado<never> => ({ ok: false, error });

type Usuario = Omit<UsuarioSistema, 'passwordHash'>;
type Tabla = 'usuarios' | 'paquetes' | 'guias' | 'transportes' | 'clientes' | 'salidas' | 'reservas' | 'pasajeros' | 'pagos';
const TODAS: Tabla[] = ['usuarios', 'paquetes', 'guias', 'transportes', 'clientes', 'salidas', 'reservas', 'pasajeros', 'pagos'];

/**
 * Datos del sistema servidos por el backend (Spring Boot + Oracle).
 * Las tablas se cargan en signals (lecturas síncronas para las pantallas) y cada operación
 * llama a la API y recarga solo las tablas que cambia. Las reglas de negocio (cupos, pagos,
 * anulación/reembolso, choques de guía/transporte) se validan en el backend.
 */
@Injectable({ providedIn: 'root' })
export class TourStore {
  private http = inject(HttpClient);

  private _usuarios = signal<Usuario[]>([]);
  private _paquetes = signal<PaqueteTour[]>([]);
  private _guias = signal<Guia[]>([]);
  private _transportes = signal<Transporte[]>([]);
  private _clientes = signal<Cliente[]>([]);
  private _salidas = signal<SalidaProgramada[]>([]);
  private _reservas = signal<Reserva[]>([]);
  private _pasajeros = signal<PasajeroReserva[]>([]);
  private _pagos = signal<Pago[]>([]);

  private fuentes: Record<Tabla, WritableSignal<any[]>> = {
    usuarios: this._usuarios, paquetes: this._paquetes, guias: this._guias, transportes: this._transportes,
    clientes: this._clientes, salidas: this._salidas, reservas: this._reservas, pasajeros: this._pasajeros, pagos: this._pagos
  };

  usuarios = this._usuarios.asReadonly();
  paquetes = this._paquetes.asReadonly();
  guias = this._guias.asReadonly();
  transportes = this._transportes.asReadonly();
  clientes = this._clientes.asReadonly();
  salidas = this._salidas.asReadonly();
  reservas = this._reservas.asReadonly();
  pasajeros = this._pasajeros.asReadonly();
  pagos = this._pagos.asReadonly();

  /** true mientras hay una operación de escritura en curso (evita doble envío). */
  ocupado = signal(false);
  private carga: Promise<Resultado> | null = null;

  // ---------- carga ----------
  /** Carga todas las tablas una vez (forzar = volver a pedirlas). */
  cargar(forzar = false): Promise<Resultado> {
    if (!this.carga || forzar) {
      this.carga = this.recargar(...TODAS).then(() => ok(), e => { this.carga = null; return fail(mensajeError(e)); });
    }
    return this.carga;
  }

  /** Olvida los datos (al cerrar sesión). */
  limpiar() {
    this.carga = null;
    TODAS.forEach(t => this.fuentes[t].set([]));
  }

  private async recargar(...tablas: Tabla[]) {
    await Promise.all(tablas.map(async t => this.fuentes[t].set(await firstValueFrom(this.http.get<any[]>(`${API}/${t}`)))));
  }

  /** Ejecuta la petición y recarga las tablas afectadas; los errores del backend vuelven como Resultado. */
  private async ejecutar<T>(peticion: Observable<T>, ...afectadas: Tabla[]): Promise<Resultado<T>> {
    this.ocupado.set(true);
    try {
      const data = await firstValueFrom(peticion);
      await this.recargar(...afectadas);
      return ok(data);
    } catch (e) {
      return fail(mensajeError(e));
    } finally {
      this.ocupado.set(false);
    }
  }

  private guardar<T>(recurso: string, d: object, id?: number) {
    return id ? this.http.put<T>(`${API}/${recurso}/${id}`, d) : this.http.post<T>(`${API}/${recurso}`, d);
  }

  // ---------- lecturas ----------
  getPaquete(id?: number | null) { return id == null ? undefined : this._paquetes().find(p => p.paqueteId === id); }
  getGuia(id?: number | null) { return id == null ? undefined : this._guias().find(g => g.guiaId === id); }
  getTransporte(id?: number | null) { return id == null ? undefined : this._transportes().find(t => t.transporteId === id); }
  getCliente(id?: number | null) { return id == null ? undefined : this._clientes().find(c => c.clienteId === id); }
  getSalida(id?: number | null) { return id == null ? undefined : this._salidas().find(s => s.salidaId === id); }
  getReserva(id?: number | null) { return id == null ? undefined : this._reservas().find(r => r.reservaId === id); }

  pasajerosDe(reservaId: number) { return this._pasajeros().filter(p => p.reservaId === reservaId); }
  pagosDe(reservaId: number) { return this._pagos().filter(p => p.reservaId === reservaId); }
  salidasDe(paqueteId: number) { return this._salidas().filter(s => s.paqueteId === paqueteId); }
  usernameDe(id?: number | null): string {
    return this._usuarios().find(u => u.usuarioId === id)?.username ?? 'usuario #' + id;
  }

  codigoReserva(id: number) { return 'R-' + String(id).padStart(4, '0'); }
  saldo(r: Reserva): number {
    return r.estadoReserva === 'ANULADA' ? 0 : Math.max(redondear(r.montoTotal - r.montoPagado), 0);
  }
  cuposOcupados(salidaId: number): number {
    return this._reservas()
      .filter(r => r.salidaId === salidaId && r.estadoReserva !== 'ANULADA')
      .reduce((a, r) => a + r.numeroViajeros, 0);
  }

  // ---------- paquetes ----------
  guardarPaquete(d: Omit<PaqueteTour, 'paqueteId'>, id?: number) {
    return this.ejecutar(this.guardar<PaqueteTour>('paquetes', d, id), 'paquetes');
  }
  eliminarPaquete(id: number) {
    return this.ejecutar(this.http.delete<void>(`${API}/paquetes/${id}`), 'paquetes');
  }

  // ---------- guías / transporte / clientes ----------
  guardarGuia(d: Omit<Guia, 'guiaId'>, id?: number) {
    return this.ejecutar(this.guardar<Guia>('guias', d, id), 'guias');
  }
  eliminarGuia(id: number) {
    return this.ejecutar(this.http.delete<void>(`${API}/guias/${id}`), 'guias');
  }
  guardarTransporte(d: Omit<Transporte, 'transporteId'>, id?: number) {
    return this.ejecutar(this.guardar<Transporte>('transportes', d, id), 'transportes');
  }
  eliminarTransporte(id: number) {
    return this.ejecutar(this.http.delete<void>(`${API}/transportes/${id}`), 'transportes');
  }
  guardarCliente(d: Omit<Cliente, 'clienteId'>, id?: number) {
    return this.ejecutar(this.guardar<Cliente>('clientes', d, id), 'clientes');
  }
  eliminarCliente(id: number) {
    return this.ejecutar(this.http.delete<void>(`${API}/clientes/${id}`), 'clientes');
  }

  // ---------- salidas programadas ----------
  guardarSalida(d: SalidaForm, id?: number) {
    return this.ejecutar(this.guardar<SalidaProgramada>('salidas', d, id), 'salidas');
  }
  eliminarSalida(id: number) {
    return this.ejecutar(this.http.delete<void>(`${API}/salidas/${id}`), 'salidas');
  }

  // ---------- reservas ----------
  crearReserva(dto: { clienteId: number; salidaId: number; observaciones: string; pasajeros: PasajeroDraft[] }) {
    const cuerpo = { ...dto, pasajeros: dto.pasajeros.map(p => ({ ...p, fechaNacimiento: p.fechaNacimiento || null })) };
    return this.ejecutar(this.http.post<Reserva>(`${API}/reservas`, cuerpo), 'reservas', 'pasajeros', 'salidas');
  }

  confirmarReserva(id: number) {
    return this.ejecutar(this.http.patch<Reserva>(`${API}/reservas/${id}/confirmar`, {}), 'reservas');
  }

  completarReserva(id: number) {
    return this.ejecutar(this.http.patch<Reserva>(`${API}/reservas/${id}/completar`, {}), 'reservas');
  }

  /** El backend toma usuario_anula_id de la sesión (token). */
  anularReserva(id: number, formaReembolso?: string) {
    return this.ejecutar(this.http.patch<Reserva>(`${API}/reservas/${id}/anular`, { formaReembolso: formaReembolso ?? null }),
      'reservas', 'pagos', 'salidas');
  }

  // ---------- pagos ----------
  registrarPago(dto: {
    reservaId: number; monto: number; moneda: Moneda; metodoPago: string;
    codigoTransaccion: string; estadoTransaccion: EstadoTransaccion;
  }) {
    return this.ejecutar(this.http.post<Pago>(`${API}/pagos`, dto), 'pagos', 'reservas');
  }

  resolverPago(pagoId: number, estado: 'APROBADO' | 'RECHAZADO') {
    return this.ejecutar(this.http.patch<Pago>(`${API}/pagos/${pagoId}/resolver`, { estado }), 'pagos', 'reservas');
  }
}
