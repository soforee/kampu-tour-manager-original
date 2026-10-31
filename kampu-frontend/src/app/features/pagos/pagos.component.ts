import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TourStore } from '../../core/services/tour-store.service';
import { AlertService } from '../../core/services/alert.service';
import { aPEN } from '../../core/utils/moneda';
import { BadgeComponent } from '../../shared/badge.component';
import { IconComponent } from '../../shared/icon.component';

@Component({
  selector: 'app-pagos',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe, BadgeComponent, IconComponent],
  templateUrl: './pagos.component.html',
  styleUrl: './pagos.component.css'
})
export class PagosComponent {
  private store = inject(TourStore);
  private alerts = inject(AlertService);

  texto = signal('');
  estado = signal('');
  estados = [{ valor: '', texto: 'Todos' }, { valor: 'APROBADO', texto: 'Aprobados' }, { valor: 'PENDIENTE', texto: 'Pendientes' },
             { valor: 'RECHAZADO', texto: 'Rechazados' }, { valor: 'REEMBOLSADO', texto: 'Reembolsados' }];

  filas = computed(() => {
    const t = this.texto().toLowerCase().trim();
    const e = this.estado();
    return this.store.pagos()
      .map(p => ({
        pago: p,
        codigo: this.store.codigoReserva(p.reservaId),
        cliente: this.store.getCliente(this.store.getReserva(p.reservaId)?.clienteId)?.nombreCompleto ?? '—',
        pen: aPEN(p.monto, p.moneda)
      }))
      .filter(f => !e || f.pago.estadoTransaccion === e)
      .filter(f => !t || f.codigo.toLowerCase().includes(t) || f.cliente.toLowerCase().includes(t)
        || f.pago.codigoTransaccion.toLowerCase().includes(t))
      .sort((a, b) => b.pago.fechaPago.localeCompare(a.pago.fechaPago));
  });

  totalAprobado = computed(() =>
    this.filas().filter(f => f.pago.estadoTransaccion === 'APROBADO').reduce((a, f) => a + f.pen, 0));

  async resolver(id: number, estado: 'APROBADO' | 'RECHAZADO') {
    if (this.store.ocupado()) return;
    const res = await this.store.resolverPago(id, estado);
    if (!res.ok) this.alerts.error(res.error!);
    else this.alerts.toast(estado === 'APROBADO' ? 'Pago aprobado' : 'Pago rechazado');
  }
}
