import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-badge',
  standalone: true,
  templateUrl: './badge.component.html',
  styleUrl: './badge.component.css'
})
export class BadgeComponent {
  @Input({ required: true }) estado = '';

  private mapa: Record<string, string> = {
    PENDIENTE: 'bg-amber-50 text-amber-700',
    PARCIAL: 'bg-sky-50 text-sky-700',
    PROGRAMADA: 'bg-sky-50 text-sky-700',
    CONFIRMADA: 'bg-emerald-50 text-emerald-700',
    APROBADO: 'bg-emerald-50 text-emerald-700',
    PAGADO: 'bg-emerald-50 text-emerald-700',
    COMPLETADA: 'bg-zinc-100 text-zinc-700',
    ANULADA: 'bg-rose-50 text-rose-700',
    CANCELADA: 'bg-rose-50 text-rose-700',
    RECHAZADO: 'bg-rose-50 text-rose-700',
    REEMBOLSADO: 'bg-violet-50 text-violet-700',
    ACTIVO: 'bg-emerald-50 text-emerald-700',
    INACTIVO: 'bg-zinc-100 text-zinc-500'
  };

  get clase() { return this.mapa[this.estado] ?? 'bg-zinc-100 text-zinc-700'; }
  get texto() { return this.estado.replace('_', ' '); }
}
