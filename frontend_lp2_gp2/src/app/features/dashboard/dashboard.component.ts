import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TourStore } from '../../core/services/tour-store.service';
import { AuthService } from '../../core/services/auth.service';
import { hoy } from '../../core/utils/date.util';
import { BadgeComponent } from '../../shared/badge.component';
import { IconComponent } from '../../shared/icon.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe, BadgeComponent, IconComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  private store = inject(TourStore);
  auth = inject(AuthService);

  saludo = computed(() => {
    const h = new Date().getHours();
    return h < 12 ? 'Buenos días' : h < 19 ? 'Buenas tardes' : 'Buenas noches';
  });

  kpis = computed(() => {
    const t = hoy();
    const activas = this.store.reservas().filter(r => r.estadoReserva !== 'ANULADA');
    return {
      salidas: this.store.salidas().filter(s => (s.estado === 'PROGRAMADA' || s.estado === 'CONFIRMADA') && s.fechaInicio >= t).length,
      reservas: activas.filter(r => r.estadoReserva === 'PENDIENTE' || r.estadoReserva === 'CONFIRMADA').length,
      cobrado: activas.reduce((a, r) => a + r.montoPagado, 0),
      porCobrar: activas.reduce((a, r) => a + this.store.saldo(r), 0)
    };
  });

  proximas = computed(() => {
    const t = hoy();
    return this.store.salidas()
      .filter(s => (s.estado === 'PROGRAMADA' || s.estado === 'CONFIRMADA') && s.fechaInicio >= t)
      .sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio))
      .slice(0, 5)
      .map(s => ({
        salida: s,
        paquete: this.store.getPaquete(s.paqueteId),
        guia: this.store.getGuia(s.guiaId),
        pct: Math.round((s.cuposMaximos - s.cuposDisponibles) * 100 / s.cuposMaximos)
      }));
  });

  recientes = computed(() =>
    [...this.store.reservas()]
      .sort((a, b) => b.fechaReserva.localeCompare(a.fechaReserva))
      .slice(0, 5)
      .map(r => ({
        reserva: r,
        codigo: this.store.codigoReserva(r.reservaId),
        cliente: this.store.getCliente(r.clienteId),
        paquete: this.store.getPaquete(this.store.getSalida(r.salidaId)?.paqueteId)
      })));
}
