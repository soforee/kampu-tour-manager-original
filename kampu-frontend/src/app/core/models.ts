// Modelos alineados al esquema Oracle (SALIDAS_PROGRAMADAS, RESERVAS, PASAJEROS_RESERVA, PAGOS)
// Las tablas PAQUETES_TOUR, GUIAS, TRANSPORTE, CLIENTES y USUARIOS_SISTEMA se modelan con campos mínimos.

export type EstadoSalida = 'PROGRAMADA' | 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';
export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'ANULADA' | 'COMPLETADA';
export type EstadoPagoReserva = 'PENDIENTE' | 'PARCIAL' | 'PAGADO' | 'REEMBOLSADO';
export type EstadoTransaccion = 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | 'REEMBOLSADO';
export type TipoDocumento = 'DNI' | 'CE' | 'PASAPORTE';
export type Moneda = 'PEN' | 'USD' | 'EUR';
export type Rol = 'ADMIN' | 'VENTAS';
export type EstadoRegistro = 'ACTIVO' | 'INACTIVO';

export interface UsuarioSistema {
  usuarioId: number;
  username: string;
  passwordHash: string; // BCrypt (nunca se envía al frontend)
  rol: Rol;
  estado: EstadoRegistro;
}

export interface PaqueteTour {
  paqueteId: number;
  nombre: string;       // máx. 100
  descripcion: string;  // máx. 1000
  precioBase: number;   // por persona, en PEN
  dias: number;
  noches: number;
  imagen: string;       // URL, máx. 500
  estado: EstadoRegistro;
}

export interface Guia {
  guiaId: number;
  nombreCompleto: string;
  especialidad: string;
  telefono: string;
}

export interface Transporte {
  transporteId: number;
  placa: string;        // única
  marcaModelo: string;
  capacidad: number;
}

export interface Cliente {
  clienteId: number;
  nombreCompleto: string;
  tipoDocumento: TipoDocumento;
  numeroDocumento: string; // único
  email: string;
  telefono: string;
}

export interface SalidaProgramada {
  salidaId: number;
  paqueteId: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;
  cuposMaximos: number;
  cuposDisponibles: number;
  guiaId?: number | null;
  transporteId?: number | null;
  estado: EstadoSalida;
}

export interface Reserva {
  reservaId: number;
  clienteId: number;
  salidaId: number;
  fechaReserva: string; // ISO timestamp
  numeroViajeros: number;
  montoTotal: number;
  montoPagado: number;
  estadoReserva: EstadoReserva;
  estadoPago: EstadoPagoReserva;
  observaciones: string;
  formaReembolso?: string;
  usuarioAnulaId?: number;
}

export interface PasajeroReserva {
  pasajeroId: number;
  reservaId: number;
  nombreCompleto: string;
  tipoDocumento: TipoDocumento;
  numeroDocumento: string;
  nacionalidad: string;
  fechaNacimiento: string; // yyyy-MM-dd
}

export interface Pago {
  pagoId: number;
  reservaId: number;
  monto: number;
  moneda: Moneda;
  metodoPago: string;
  codigoTransaccion: string;
  fechaPago: string; // ISO timestamp
  estadoTransaccion: EstadoTransaccion;
}

export interface Resultado<T = unknown> {
  ok: boolean;
  error?: string;
  data?: T;
}

export type PasajeroDraft = Omit<PasajeroReserva, 'pasajeroId' | 'reservaId'>;
export type SalidaForm = Omit<SalidaProgramada, 'salidaId' | 'cuposDisponibles'>;
