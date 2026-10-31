function pad(n: number): string {
  return n < 10 ? '0' + n : '' + n;
}

/** Fecha local en formato yyyy-MM-dd */
export function toIsoDate(d: Date): string {
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

export function hoy(): string {
  return toIsoDate(new Date());
}

export function addDays(iso: string, days: number): string {
  const [y, m, d] = iso.split('-').map(Number);
  return toIsoDate(new Date(y, m - 1, d + days));
}
