import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TourStore } from '../../core/services/tour-store.service';
import { BadgeComponent } from '../../shared/badge.component';
import { IconComponent } from '../../shared/icon.component';

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe, BadgeComponent, IconComponent],
  templateUrl: './reservas.component.html',
  styleUrl: './reservas.component.css'
})
export class ReservasComponent {
  private store = inject(TourStore);
  private route = inject(ActivatedRoute);

  texto = signal('');
  estado = signal('');
  estados = [
    { valor: '', texto: 'Todas' }, { valor: 'PENDIENTE', texto: 'Pendientes' },
    { valor: 'CONFIRMADA', texto: 'Confirmadas' }, { valor: 'COMPLETADA', texto: 'Completadas' }, { valor: 'ANULADA', texto: 'Anuladas' }
  ];

  constructor() {
    this.route.queryParamMap.pipe(takeUntilDestroyed()).subscribe(p => this.texto.set(p.get('q') ?? ''));
  }

  filas = computed(() => {
    const t = this.texto().toLowerCase().trim();
    const e = this.estado();
    return this.store.reservas()
      .map(r => {
        const salida = this.store.getSalida(r.salidaId);
        return {
          reserva: r, salida,
          codigo: this.store.codigoReserva(r.reservaId),
          cliente: this.store.getCliente(r.clienteId),
          paquete: this.store.getPaquete(salida?.paqueteId),
          saldo: this.store.saldo(r)
        };
      })
      .filter(f => !e || f.reserva.estadoReserva === e)
      .filter(f => !t || f.codigo.toLowerCase().includes(t)
        || (f.cliente?.nombreCompleto.toLowerCase().includes(t) ?? false)
        || (f.paquete?.nombre.toLowerCase().includes(t) ?? false))
      .sort((a, b) => b.reserva.reservaId - a.reserva.reservaId);
  });
}
