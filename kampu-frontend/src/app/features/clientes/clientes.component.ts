import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TourStore } from '../../core/services/tour-store.service';
import { AlertService } from '../../core/services/alert.service';
import { Cliente, TipoDocumento } from '../../core/models';
import { IconComponent } from '../../shared/icon.component';
import { ModalComponent } from '../../shared/modal.component';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [FormsModule, IconComponent, ModalComponent],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.css'
})
export class ClientesComponent {
  private store = inject(TourStore);
  private alerts = inject(AlertService);

  tipos: TipoDocumento[] = ['DNI', 'CE', 'PASAPORTE'];
  texto = signal('');
  abierto = signal(false);
  editId: number | null = null;
  form: Omit<Cliente, 'clienteId'> = this.vacio();

  private vacio() {
    return { nombreCompleto: '', tipoDocumento: 'DNI' as TipoDocumento, numeroDocumento: '', email: '', telefono: '' };
  }

  filas = computed(() => {
    const t = this.texto().toLowerCase().trim();
    return this.store.clientes()
      .filter(c => !t || c.nombreCompleto.toLowerCase().includes(t) || c.numeroDocumento.includes(t))
      .map(c => ({ cliente: c, reservas: this.store.reservas().filter(r => r.clienteId === c.clienteId).length }));
  });

  nuevo() { this.editId = null; this.form = this.vacio(); this.abierto.set(true); }

  editar(id: number) {
    const { clienteId, ...resto } = this.store.getCliente(id)!;
    this.editId = clienteId;
    this.form = { ...resto };
    this.abierto.set(true);
  }

  async guardar() {
    if (this.store.ocupado()) return;
    const f = this.form;
    if (!f.nombreCompleto.trim() || !f.numeroDocumento.trim()) { this.alerts.error('Nombre y documento son obligatorios.'); return; }
    const res = await this.store.guardarCliente({ ...f, nombreCompleto: f.nombreCompleto.trim(), numeroDocumento: f.numeroDocumento.trim() }, this.editId ?? undefined);
    if (!res.ok) { this.alerts.error(res.error!); return; }
    this.abierto.set(false);
    this.alerts.toast(this.editId ? 'Cliente actualizado' : 'Cliente registrado');
  }

  async eliminar(id: number) {
    if (!(await this.alerts.confirm('Esta acción no se puede deshacer.', 'Eliminar cliente'))) return;
    const res = await this.store.eliminarCliente(id);
    if (!res.ok) this.alerts.error(res.error!); else this.alerts.toast('Cliente eliminado');
  }
}
