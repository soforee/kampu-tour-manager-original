import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TourStore } from '../../core/services/tour-store.service';
import { AlertService } from '../../core/services/alert.service';
import { EstadoTransaccion, Moneda } from '../../core/models';
import { aPEN, TASAS } from '../../core/utils/moneda';
import { BadgeComponent } from '../../shared/badge.component';
import { IconComponent } from '../../shared/icon.component';

@Component({
  selector: 'app-reserva-detalle',
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe, DatePipe, BadgeComponent, IconComponent],
  templateUrl: './reserva-detalle.component.html',
  styleUrl: './reserva-detalle.component.css'
})
export class ReservaDetalleComponent {
  private store = inject(TourStore);
  private alerts = inject(AlertService);
  private router = inject(Router);
  private id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

  monedas: Moneda[] = ['PEN', 'USD', 'EUR'];
  metodos = ['Efectivo', 'Tarjeta', 'Transferencia', 'Yape', 'Plin'];
  formasReembolso = ['Transferencia', 'Efectivo', 'Tarjeta', 'Yape', 'Plin', 'Nota de crédito'];

  monto: number | null = null;
  moneda: Moneda = 'PEN';
  metodo = 'Efectivo';
  codigo = '';
  estadoTx: EstadoTransaccion = 'APROBADO';

  reserva = computed(() => this.store.getReserva(this.id));
  codReserva = computed(() => this.store.codigoReserva(this.id));
  salida = computed(() => this.store.getSalida(this.reserva()?.salidaId));
  paquete = computed(() => this.store.getPaquete(this.salida()?.paqueteId));
  guia = computed(() => this.store.getGuia(this.salida()?.guiaId));
  transporte = computed(() => this.store.getTransporte(this.salida()?.transporteId));
  cliente = computed(() => this.store.getCliente(this.reserva()?.clienteId));
  pasajeros = computed(() => this.store.pasajerosDe(this.id));
  pagos = computed(() => this.store.pagosDe(this.id));
  saldo = computed(() => { const r = this.reserva(); return r ? this.store.saldo(r) : 0; });
  avance = computed(() => { const r = this.reserva(); return r && r.montoTotal ? Math.min(100, r.montoPagado * 100 / r.montoTotal) : 0; });
  puedePagar = computed(() => {
    const r = this.reserva();
    return !!r && (r.estadoReserva === 'PENDIENTE' || r.estadoReserva === 'CONFIRMADA') && this.saldo() > 0;
  });
  usuarioAnula = computed(() => this.store.usernameDe(this.reserva()?.usuarioAnulaId));

  constructor() {
    this.store.cargar().then(r => { if (r.ok && !this.store.getReserva(this.id)) this.router.navigate(['/reservas']); });
  }

  tasa() { return TASAS[this.moneda]; }
  equivalente() { return aPEN(this.monto ?? 0, this.moneda); }

  usarSaldo() {
    // saldo en PEN convertido a la moneda elegida
    this.monto = Math.round((this.saldo() / TASAS[this.moneda] + Number.EPSILON) * 100) / 100;
  }

  private async resultado(peticion: Promise<{ ok: boolean; error?: string }>, mensaje: string) {
    const res = await peticion;
    if (!res.ok) this.alerts.error(res.error!);
    else this.alerts.toast(mensaje);
    return res.ok;
  }

  async confirmar() {
    if (await this.alerts.confirm('La reserva pasará a CONFIRMADA.', 'Confirmar reserva'))
      this.resultado(this.store.confirmarReserva(this.id), 'Reserva confirmada');
  }

  async completar() {
    if (await this.alerts.confirm('La reserva pasará a COMPLETADA.', 'Completar reserva'))
      this.resultado(this.store.completarReserva(this.id), 'Reserva completada');
  }

  async anular() {
    const r = this.reserva();
    if (!r) return;
    let forma: string | undefined;
    if (r.montoPagado > 0) {
      const sel = await this.alerts.select('Anular reserva',
        `Se reembolsarán S/ ${r.montoPagado.toFixed(2)} ya pagados. ¿Cómo se devolverá el dinero?`, this.formasReembolso);
      if (!sel) return;
      forma = sel;
    } else if (!(await this.alerts.confirm('Se liberarán los cupos de la salida.', 'Anular reserva', 'Anular'))) {
      return;
    }
    this.resultado(this.store.anularReserva(this.id, forma), 'Reserva anulada');
  }

  async registrarPago() {
    if (this.store.ocupado()) return;
    const ok = await this.resultado(this.store.registrarPago({
      reservaId: this.id, monto: this.monto ?? 0, moneda: this.moneda, metodoPago: this.metodo,
      codigoTransaccion: this.codigo, estadoTransaccion: this.estadoTx
    }), 'Pago registrado');
    if (ok) { this.monto = null; this.codigo = ''; this.estadoTx = 'APROBADO'; }
  }

  resolver(pagoId: number, estado: 'APROBADO' | 'RECHAZADO') {
    if (this.store.ocupado()) return;
    this.resultado(this.store.resolverPago(pagoId, estado), estado === 'APROBADO' ? 'Pago aprobado' : 'Pago rechazado');
  }
}
