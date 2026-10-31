import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TourStore } from '../../core/services/tour-store.service';
import { AlertService } from '../../core/services/alert.service';
import { IconComponent } from '../../shared/icon.component';
import { ModalComponent } from '../../shared/modal.component';

@Component({
  selector: 'app-recursos',
  standalone: true,
  imports: [FormsModule, IconComponent, ModalComponent],
  templateUrl: './recursos.component.html',
  styleUrl: './recursos.component.css'
})
export class RecursosComponent {
  store = inject(TourStore);
  private alerts = inject(AlertService);

  tab = signal<'guias' | 'transporte'>('guias');
  abierto = signal(false);
  editId: number | null = null;
  guia = { nombreCompleto: '', especialidad: '', telefono: '' };
  transp = { placa: '', marcaModelo: '', capacidad: 10 };

  nuevo() {
    this.editId = null;
    this.guia = { nombreCompleto: '', especialidad: '', telefono: '' };
    this.transp = { placa: '', marcaModelo: '', capacidad: 10 };
    this.abierto.set(true);
  }

  editarGuia(id: number) {
    const { guiaId, ...resto } = this.store.getGuia(id)!;
    this.editId = guiaId; this.guia = { ...resto }; this.abierto.set(true);
  }

  editarTransporte(id: number) {
    const { transporteId, ...resto } = this.store.getTransporte(id)!;
    this.editId = transporteId; this.transp = { ...resto }; this.abierto.set(true);
  }

  async guardar() {
    if (this.store.ocupado()) return;
    if (this.tab() === 'guias') {
      if (!this.guia.nombreCompleto.trim()) { this.alerts.error('El nombre completo es obligatorio.'); return; }
      const res = await this.store.guardarGuia(this.guia, this.editId ?? undefined);
      if (!res.ok) { this.alerts.error(res.error!); return; }
    } else {
      if (!this.transp.placa.trim() || !(this.transp.capacidad >= 1 && this.transp.capacidad <= 999)) {
        this.alerts.error('La placa es obligatoria y la capacidad debe estar entre 1 y 999.'); return;
      }
      const res = await this.store.guardarTransporte(this.transp, this.editId ?? undefined);
      if (!res.ok) { this.alerts.error(res.error!); return; }
    }
    this.abierto.set(false);
    this.alerts.toast('Guardado');
  }

  async eliminarGuia(id: number) {
    if (!(await this.alerts.confirm('Esta acción no se puede deshacer.', 'Eliminar guía'))) return;
    const res = await this.store.eliminarGuia(id);
    if (!res.ok) this.alerts.error(res.error!); else this.alerts.toast('Guía eliminado');
  }

  async eliminarTransporte(id: number) {
    if (!(await this.alerts.confirm('Esta acción no se puede deshacer.', 'Eliminar transporte'))) return;
    const res = await this.store.eliminarTransporte(id);
    if (!res.ok) this.alerts.error(res.error!); else this.alerts.toast('Transporte eliminado');
  }
}
