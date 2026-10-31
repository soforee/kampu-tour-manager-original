import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TourStore } from '../../core/services/tour-store.service';
import { AlertService } from '../../core/services/alert.service';
import { PasajeroDraft, TipoDocumento } from '../../core/models';
import { hoy } from '../../core/utils/date.util';
import { IconComponent } from '../../shared/icon.component';

@Component({
  selector: 'app-reserva-nueva',
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe, DatePipe, IconComponent],
  templateUrl: './reserva-nueva.component.html',
  styleUrl: './reserva-nueva.component.css'
})
export class ReservaNuevaComponent implements OnInit {
  store = inject(TourStore);
  private alerts = inject(AlertService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  tipos: TipoDocumento[] = ['DNI', 'CE', 'PASAPORTE'];
  clienteId: number | null = null;
  salidaId: number | null = null;
  observaciones = '';
  pasajeros: PasajeroDraft[] = [this.vacio()];

  /** Fecha máxima de nacimiento: la BD exige fecha_nacimiento < hoy */
  ayer = (() => { const d = new Date(); d.setDate(d.getDate() - 1); return d.toISOString().slice(0, 10); })();

  private vacio(): PasajeroDraft {
    return { nombreCompleto: '', tipoDocumento: 'DNI', numeroDocumento: '', nacionalidad: 'Peruana', fechaNacimiento: '' };
  }

  get salidasDisponibles() {
    const t = hoy();
    return this.store.salidas()
      .filter(s => (s.estado === 'PROGRAMADA' || s.estado === 'CONFIRMADA') && s.fechaInicio >= t && s.cuposDisponibles > 0)
      .sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio));
  }
  get salida() { return this.store.getSalida(this.salidaId); }
  get paquete() { return this.store.getPaquete(this.salida?.paqueteId); }
  get total() { return (this.paquete?.precioBase ?? 0) * this.pasajeros.length; }
  get excede() { return !!this.salida && this.pasajeros.length > this.salida.cuposDisponibles; }
  get puedeGuardar() { return !this.store.ocupado() && !!this.clienteId && !!this.salida && !this.excede && this.pasajeros.length > 0; }

  async ngOnInit() {
    await this.store.cargar(); // asegura salidas/paquetes cargados antes de preseleccionar
    const q = this.route.snapshot.queryParamMap;
    const idSalida = Number(q.get('salida')) || null;
    const idPaquete = Number(q.get('paquete')) || null;
    if (idSalida) this.salidaId = idSalida;
    else if (idPaquete) this.salidaId = this.salidasDisponibles.find(s => s.paqueteId === idPaquete)?.salidaId ?? null;
  }

  agregar() { if (this.pasajeros.length < 99) this.pasajeros.push(this.vacio()); }
  quitar(i: number) { this.pasajeros.splice(i, 1); }

  agregarCliente() {
    const c = this.store.getCliente(this.clienteId);
    if (!c) return;
    const dato: PasajeroDraft = {
      nombreCompleto: c.nombreCompleto, tipoDocumento: c.tipoDocumento, numeroDocumento: c.numeroDocumento,
      nacionalidad: 'Peruana', fechaNacimiento: ''
    };
    const primero = this.pasajeros[0];
    if (this.pasajeros.length === 1 && !primero.nombreCompleto && !primero.numeroDocumento) this.pasajeros[0] = dato;
    else this.pasajeros.push(dato);
  }

  async guardar() {
    if (!this.puedeGuardar) return;
    const res = await this.store.crearReserva({
      clienteId: this.clienteId!, salidaId: this.salidaId!,
      observaciones: this.observaciones, pasajeros: this.pasajeros
    });
    if (!res.ok) { this.alerts.error(res.error!); return; }
    this.alerts.toast('Reserva creada');
    this.router.navigate(['/reservas', res.data!.reservaId]);
  }
}
