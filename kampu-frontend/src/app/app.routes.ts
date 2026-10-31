import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/login/login.component';
import { ShellComponent } from './layout/shell.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ReservasComponent } from './features/reservas/reservas.component';
import { ReservaNuevaComponent } from './features/reservas/reserva-nueva.component';
import { ReservaDetalleComponent } from './features/reservas/reserva-detalle.component';
import { SalidasComponent } from './features/salidas/salidas.component';
import { PaquetesComponent } from './features/paquetes/paquetes.component';
import { PagosComponent } from './features/pagos/pagos.component';
import { ClientesComponent } from './features/clientes/clientes.component';
import { RecursosComponent } from './features/recursos/recursos.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [loginGuard] },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'reservas', component: ReservasComponent },
      { path: 'reservas/nueva', component: ReservaNuevaComponent },
      { path: 'reservas/:id', component: ReservaDetalleComponent },
      { path: 'salidas', component: SalidasComponent },
      { path: 'paquetes', component: PaquetesComponent },
      { path: 'pagos', component: PagosComponent },
      { path: 'clientes', component: ClientesComponent },
      { path: 'recursos', component: RecursosComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
