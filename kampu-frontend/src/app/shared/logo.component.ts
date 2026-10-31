import { Component, Input } from '@angular/core';

/**
 * Isotipo KAMPU (carpa + sol + cima + base) y logotipo.
 * variante: 'isotipo' | 'horizontal' | 'vertical'. oscuro = versión para fondos verde bosque.
 * alto = alto del isotipo en px.
 */
@Component({
  selector: 'app-logo',
  standalone: true,
  templateUrl: './logo.component.html',
  styleUrl: './logo.component.css'
})
export class LogoComponent {
  @Input() variante: 'isotipo' | 'horizontal' | 'vertical' = 'horizontal';
  @Input() oscuro = false;
  @Input() alto = 32;
  /** Color de la carpa (para usar el logo sobre fotografías) */
  @Input() colorCarpa?: string;

  // sobre fondo oscuro la carpa va un poco más clara que el fondo, como en la identidad
  get carpa() { return this.colorCarpa ?? (this.oscuro ? '#24472F' : '#1F3D2B'); }
}
