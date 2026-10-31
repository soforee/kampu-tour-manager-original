import { Moneda } from '../models';

// Tipos de cambio de demostración (1 unidad -> PEN)
export const TASAS: Record<Moneda, number> = { PEN: 1, USD: 3.75, EUR: 4.05 };

export function aPEN(monto: number, moneda: Moneda): number {
  return redondear(monto * TASAS[moneda]);
}

export function redondear(n: number): number {
  return Math.round((n + Number.EPSILON) * 100) / 100;
}
