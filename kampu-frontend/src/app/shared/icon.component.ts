import { Component, Input } from '@angular/core';

/** Iconos SVG de trazo (estilo Feather) sin dependencias externas. */
@Component({
  selector: 'app-icon',
  standalone: true,
  templateUrl: './icon.component.html',
  styleUrl: './icon.component.css'
})
export class IconComponent {
  @Input({ required: true }) name = '';
  @Input() size = 'w-5 h-5';
}
