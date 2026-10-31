import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { IconComponent } from '../../shared/icon.component';
import { LogoComponent } from '../../shared/logo.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, IconComponent, LogoComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  ver = signal(false);
  error = signal('');
  cargando = signal(false);

  rellenar(username: string, password: string) {
    this.username = username;
    this.password = password;
    this.error.set('');
  }

  async entrar() {
    if (!this.username.trim() || !this.password) {
      this.error.set('Ingresa tu usuario y contraseña.');
      return;
    }
    this.cargando.set(true);
    const error = await this.auth.login(this.username, this.password);
    if (!error) {
      this.router.navigate(['/dashboard']);
    } else {
      this.error.set(error);
      this.cargando.set(false);
    }
  }
}
