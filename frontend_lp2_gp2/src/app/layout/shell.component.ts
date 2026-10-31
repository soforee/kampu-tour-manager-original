import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { TourStore } from '../core/services/tour-store.service';
import { AlertService } from '../core/services/alert.service';
import { IconComponent } from '../shared/icon.component';
import { LogoComponent } from '../shared/logo.component';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IconComponent, LogoComponent],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css'
})
export class ShellComponent {
  auth = inject(AuthService);
  private store = inject(TourStore);
  private router = inject(Router);
  private alerts = inject(AlertService);

  abierto = signal(false);

  menu = [
    { ruta: '/dashboard', texto: 'Panel', icono: 'home' },
    { ruta: '/reservas', texto: 'Reservas', icono: 'ticket' },
    { ruta: '/salidas', texto: 'Salidas', icono: 'calendar' },
    { ruta: '/paquetes', texto: 'Paquetes', icono: 'package' },
    { ruta: '/pagos', texto: 'Pagos', icono: 'card' },
    { ruta: '/clientes', texto: 'Clientes', icono: 'users' },
    { ruta: '/recursos', texto: 'Guías y transporte', icono: 'truck' }
  ] as { ruta: string; texto: string; icono: string; exact?: boolean }[];

  pendientes = computed(() => this.store.reservas().filter(r => r.estadoReserva === 'PENDIENTE').length);
  iniciales = computed(() => (this.auth.usuario()?.username ?? '?').slice(0, 2).toUpperCase());

  constructor() {
    // Datos frescos del backend en cada inicio de sesión / recarga de página
    // (si la sesión venció, el interceptor ya redirigió al login: no se muestra error)
    this.store.cargar(true).then(r => {
      if (!r.ok && this.auth.autenticado()) this.alerts.error(r.error!, 'No se pudieron cargar los datos');
    });
  }

  async salir() {
    if (await this.alerts.confirm('Se cerrará tu sesión actual.', 'Cerrar sesión', 'Cerrar sesión')) {
      this.auth.logout();
      this.store.limpiar();
      this.router.navigate(['/login']);
    }
  }
}
