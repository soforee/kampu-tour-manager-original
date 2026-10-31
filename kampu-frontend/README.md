# KAMPU · Tour Manager (frontend Angular + Tailwind CDN)

Angular 18 (standalone, signals) + Tailwind por CDN + SweetAlert2. Se conecta al backend `kampu-backend` (Spring Boot + Oracle); ver el README de la raíz del proyecto.

## Ejecutar
Primero levanta Oracle y el backend (`kampu-backend`, puerto 8082). Luego:
```
npm install
npm start
```
http://localhost:4200 → login (usuarios de `02_data.sql`):
- usuario `admin` / clave `admin123`
- usuario `ventas` / clave `ventas123`

## Estructura
- `core/models.ts`: interfaces 1:1 con el esquema Oracle (USUARIOS_SISTEMA, CLIENTES, GUIAS, TRANSPORTE, PAQUETES_TOUR, SALIDAS_PROGRAMADAS, RESERVAS, PASAJEROS_RESERVA, PAGOS).
- `core/services/tour-store.service.ts`: datos del backend en signals; cada operación llama a la API (`/api/v1/...`) y recarga las tablas afectadas. Las reglas de negocio (cupos, pagos, anulación/reembolso, choques de guía/transporte) las valida el backend.
- `core/services/auth.service.ts` + `core/interceptors/auth.interceptor.ts`: login contra el backend, token en cada petición y regreso al login si la sesión vence.
- `core/utils/api.ts`: rutas base de la API/imágenes y lectura de los mensajes de error del backend.
- `public/paisaje_inicio.jpg`: foto de fondo del login.
- Cada componente tiene sus tres archivos separados: `nombre.component.ts` (lógica), `nombre.component.html` (plantilla) y `nombre.component.css` (estilos).
- `core/guards`: guard de login. `layout/`: shell con sidebar. `features/`: una pantalla por módulo.

## Reglas (las aplica el backend, según la BD)
- Reservar descuenta `cupos_disponibles`; anular los libera.
- `monto_pagado` = suma de pagos APROBADOS (convertidos a PEN con tasas demo en `core/utils/moneda.ts`); `estado_pago` se deriva (PENDIENTE/PARCIAL/PAGADO).
- Anular con dinero pagado exige `forma_reembolso`, marca los pagos como REEMBOLSADO y guarda `usuario_anula_id`.
- Validaciones: fecha_fin ≥ fecha_inicio, cupos ≤ capacidad del transporte, sin choque de guía/transporte en fechas, documento único por reserva, fecha de nacimiento < hoy, máx. 99 viajeros, observaciones ≤ 200.

## Backend
`proxy.config.json` redirige `/api` → `http://localhost:8082/lp2_gp2/api` y `/lp2_gp2/images` → `http://localhost:8082`.

## Identidad visual KAMPU
- Paleta en `src/index.html` (config de Tailwind): crema `#F4F1E8`, arena `#E6DFCD`, verde bosque `#16301F` / carpa `#1F3D2B`, oliva `#6B7A3A`, marrón `#6B4A32`.
- Tipografías: Archivo (extendida para títulos y logotipo) y JetBrains Mono (etiquetas técnicas).
- Isotipo y logotipo: `src/app/shared/logo.component.ts` (`<app-logo variante="horizontal|vertical|isotipo" [oscuro]="true" [alto]="32">`).

## Notas del esquema
- `PAQUETES_TOUR.imagen` (máx. 500): las imágenes se **suben desde el equipo** (JPG, PNG o WebP). Se redimensionan a 1280 px, se comprimen y se suben a `POST /api/v1/paquetes/imagen`; en la columna se guarda solo la ruta (`paquetes/<uuid>.jpg`) y el backend la sirve en `/lp2_gp2/images/<ruta>`.
- Login contra `USUARIOS_SISTEMA`: `username` + `password_hash` (BCrypt, verificado en el backend) + `estado` ACTIVO.
- En localStorage solo queda la sesión (`tm3_session`). Los datos `tm3_*` de la versión sin backend ya no se usan y pueden borrarse.
