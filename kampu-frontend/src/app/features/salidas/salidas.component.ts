import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TourStore } from '../../core/services/tour-store.service';
import { AlertService } from '../../core/services/alert.service';
import { EstadoSalida, SalidaForm } from '../../core/models';
import { addDays, hoy } from '../../core/utils/date.util';
import { BadgeComponent } from '../../shared/badge.component';
import { IconComponent } from '../../shared/icon.component';
import { ModalComponent } from '../../shared/modal.component';

@Component({
  selector: 'app-salidas',
  standalone: true,
  imports: [FormsModule, DatePipe, BadgeComponent, IconComponent, ModalComponent],
  templateUrl: './salidas.component.html',
  styleUrl: './salidas.component.css'
})
export class SalidasComponent {
  store = inject(TourStore);
  private alerts = inject(AlertService);

  estados: EstadoSalida[] = ['PROGRAMADA', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA'];
  filtros = [{ valor: '', texto: 'Todas' }, { valor: 'PROGRAMADA', texto: 'Programadas' }, { valor: 'CONFIRMADA', texto: 'Confirmadas' },
             { valor: 'COMPLETADA', texto: 'Completadas' }, { valor: 'CANCELADA', texto: 'Canceladas' }];
  filtro = signal('');
  abierto = signal(false);
  editId: number | null = null;
  form: SalidaForm = this.vacio();

  private vacio(): SalidaForm {
    return { paqueteId: 0, fechaInicio: hoy(), fechaFin: hoy(), cuposMaximos: 10, guiaId: null, transporteId: null, estado: 'PROGRAMADA' };
  }

  filas = computed(() => {
    const f = this.filtro();
    return this.store.salidas()
      .filter(s => !f || s.estado === f)
      .sort((a, b) => b.fechaInicio.localeCompare(a.fechaInicio))
      .map(s => ({
        salida: s,
        paquete: this.store.getPaquete(s.paqueteId),
        guia: this.store.getGuia(s.guiaId),
        transporte: this.store.getTransporte(s.transporteId),
        pct: Math.round((s.cuposMaximos - s.cuposDisponibles) * 100 / s.cuposMaximos)
      }));
  });

  nuevo() { this.editId = null; this.form = this.vacio(); this.abierto.set(true); }

  editar(id: number) {
    const { salidaId, cuposDisponibles, ...resto } = this.store.getSalida(id)!;
    this.editId = salidaId;
    this.form = { ...resto };
    this.abierto.set(true);
  }

  /** Al crear: sugiere cupos y fecha fin según el paquete */
  sugerirFin() {
    const p = this.store.getPaquete(this.form.paqueteId);
    if (p && this.form.fechaInicio) this.form.fechaFin = addDays(this.form.fechaInicio, p.dias - 1);
  }

  async guardar() {
    if (this.store.ocupado()) return;
    const res = await this.store.guardarSalida(this.form, this.editId ?? undefined);
    if (!res.ok) { this.alerts.error(res.error!); return; }
    this.abierto.set(false);
    this.alerts.toast(this.editId ? 'Salida actualizada' : 'Salida creada');
  }

  async eliminar(id: number) {
    if (!(await this.alerts.confirm('Esta acción no se puede deshacer.', 'Eliminar salida'))) return;
    const res = await this.store.eliminarSalida(id);
    if (!res.ok) this.alerts.error(res.error!); else this.alerts.toast('Salida eliminada');
  }
}
