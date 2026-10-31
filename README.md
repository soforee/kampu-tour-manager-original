# KAMPU · Tour Manager — sistema integrado (Angular + Spring Boot + Oracle)

```
proyecto/
├── lp2_gp2/            Backend Spring Boot 3.4 (Java 21) — API REST en http://localhost:8082/lp2_gp2
│   └── src/main/resources/db/
│       ├── 01_schema.sql   Script de tablas (el original; un solo ajuste, ver "Hallazgos")
│       └── 02_data.sql     Datos de demostración (los mismos que tenía el frontend en seed.ts)
├── frontend_lp2_gp2/   Frontend Angular 18 (el diseño y las pantallas no cambian)
└── pruebas-e2e/        Prueba de punta a punta con Playwright (navegador → Angular → API → Oracle)
```

## 1. Base de datos (Oracle)

1. Crea (o usa) el usuario dueño del esquema. Por defecto el backend usa `LP2` / `admin123` en `localhost:1522/XE`.
2. Conectado como ese usuario (SQL Developer o SQL*Plus), ejecuta **en orden**:
   1. `lp2_gp2/src/main/resources/db/01_schema.sql`
   2. `lp2_gp2/src/main/resources/db/02_data.sql` (opcional, carga los datos demo y hace `COMMIT`)

   En SQL Developer usa **"Ejecutar script" (F5)**, no "Ejecutar sentencia" (el trigger termina en `/`).
   En SQL*Plus, para que las tildes se guarden bien: `set NLS_LANG=AMERICAN_AMERICA.AL32UTF8` antes de entrar.
3. Si no cargas `02_data.sql`, al iniciar el backend se crean solos los usuarios `admin` y `ventas` (si la tabla está vacía).

> `02_data.sql` usa IDs fijos (para que coincidan las relaciones) y al final reinicia las identidades con
> `ALTER TABLE ... MODIFY ... (START WITH LIMIT VALUE)`, para que los registros nuevos continúen desde el último ID.

## 2. Backend (`lp2_gp2`)

Conexión en `src/main/resources/application.yaml`. Puedes editarla ahí o usar variables de entorno:

| Variable      | Valor por defecto                        |
|---------------|------------------------------------------|
| `DB_URL`      | `jdbc:oracle:thin:@localhost:1522/XE`    |
| `DB_USERNAME` | `LP2`                                    |
| `DB_PASSWORD` | `admin123`                               |

Ejemplos de URL: Oracle XE 21c → `jdbc:oracle:thin:@localhost:1521/XEPDB1`; Oracle 23 Free → `jdbc:oracle:thin:@localhost:1521/FREEPDB1`.

```bash
cd lp2_gp2
./mvnw spring-boot:run
```

(En Windows: `mvnw.cmd spring-boot:run`, o ejecutar `Lp2Gp2Application` desde IntelliJ.)

- Swagger: http://localhost:8082/lp2_gp2/swagger-ui/index.html (haz login en `POST /api/v1/auth/login`, copia el `token` y pégalo en **Authorize**).
- Imágenes de paquetes: se guardan en la carpeta `images/` (propiedad `storage.location`) y se sirven en `/lp2_gp2/images/...`.
- `spring.jpa.hibernate.ddl-auto` está en **`none`**: Hibernate no crea ni altera tablas; manda el script.
- Pruebas: `./mvnw test` (usa H2 en modo Oracle con el mismo script; no necesita Oracle).

## 3. Frontend (`frontend_lp2_gp2`)

```bash
cd frontend_lp2_gp2
npm install
npm start
```

Abre http://localhost:4200 → usuario `admin` / `admin123` (o `ventas` / `ventas123`).

`npm start` usa `proxy.config.json` (ya existía): `/api` → `http://localhost:8082/lp2_gp2/api` y
`/lp2_gp2/images` → `http://localhost:8082`. Por eso el frontend llama a rutas relativas y no hay problemas de CORS.
**Orden de arranque:** Oracle → backend → frontend.

## 4. API REST (todas bajo `/lp2_gp2/api/v1`, JSON con los mismos nombres que `core/models.ts`)

Todas exigen `Authorization: Bearer <token>`, salvo el login. Errores: `{ "status": 400, "error": "mensaje", "timestamp": ... }`
(400 regla de negocio/validación, 401 sesión, 403 usuario inactivo, 404 no existe, 409 duplicado).

| Recurso | Endpoints |
|---|---|
| Auth | `POST /auth/login` `{username,password}` → `{token, usuario}` · `POST /auth/logout` |
| Usuarios | `GET /usuarios` (sin `password_hash`) |
| Paquetes | `GET/POST /paquetes` · `GET/PUT/DELETE /paquetes/{id}` · `POST /paquetes/imagen` (multipart `file`) → `{imagen}` |
| Guías | `GET/POST /guias` · `GET/PUT/DELETE /guias/{id}` |
| Transporte | `GET/POST /transportes` · `GET/PUT/DELETE /transportes/{id}` |
| Clientes | `GET/POST /clientes` · `GET/PUT/DELETE /clientes/{id}` |
| Salidas | `GET/POST /salidas` · `GET/PUT/DELETE /salidas/{id}` |
| Reservas | `GET/POST /reservas` (POST incluye `pasajeros[]`) · `GET /reservas/{id}` · `GET /reservas/{id}/pasajeros` · `PATCH /reservas/{id}/confirmar` · `PATCH /reservas/{id}/completar` · `PATCH /reservas/{id}/anular` `{formaReembolso}` |
| Pasajeros | `GET /pasajeros[?reservaId=]` |
| Pagos | `GET /pagos[?reservaId=]` · `POST /pagos` · `PATCH /pagos/{id}/resolver` `{estado: APROBADO\|RECHAZADO}` |

Reglas que aplica el backend (las mismas que tenía el frontend en `TourStore`):
reservar descuenta `cupos_disponibles` y anular los libera (con bloqueo `SELECT ... FOR UPDATE` para evitar sobreventa);
`monto_pagado` = suma de pagos APROBADOS convertidos a PEN y `estado_pago` se deriva (PENDIENTE/PARCIAL/PAGADO);
un pago aprobado no puede exceder el saldo; anular con dinero pagado exige `forma_reembolso`, marca los pagos como
REEMBOLSADO y guarda `usuario_anula_id` (tomado de la sesión, no del navegador); fecha fin ≥ inicio; cupos ≤ capacidad
del transporte; sin cruce de guía/transporte en fechas; documento único por reserva; fecha de nacimiento < hoy;
no se eliminan registros en uso (cliente con reservas, paquete/guía/transporte con salidas, salida con reservas).

## 5. Compatibilidad verificada

| Elemento | Frontend | Backend | Base de datos |
|---|---|---|---|
| Tablas | `core/models.ts` (9 interfaces) | 9 entidades `@Table` con el nombre exacto | 9 tablas del script |
| Columnas | camelCase (`fechaInicio`) | `@Column(name="fecha_inicio")` | snake_case |
| Fechas `DATE` | `'yyyy-MM-dd'` | `LocalDate` | `DATE` |
| `TIMESTAMP` | string ISO | `LocalDateTime` | `TIMESTAMP` |
| Montos | `number` | `BigDecimal` (10,2) | `NUMBER(10,2)` |
| Estados / monedas / tipo doc. | tipos unión TS | `@Pattern` con los mismos valores | `CHECK ... IN (...)` |
| Longitudes | `maxlength` en los formularios | `@Size` | `VARCHAR2(n)` |
| Opcionales vacíos | `''` | `''` ↔ `NULL` (Oracle guarda `''` como NULL) | columnas sin `NOT NULL` |
| Tipos de cambio | `core/utils/moneda.ts` (`TASAS`) | `tour.tasas` en `application.yaml` | — (deben coincidir) |

## 6. Hallazgos y cómo se resolvieron

1. **El ZIP del backend era otra aplicación.** Contenía una plantilla de e-commerce (`producto`, `pedidos`, `persona`,
   `usuario`) sin relación con el script ni con el frontend, y con `ddl-auto: update` (habría creado esas tablas en tu esquema).
   Se reemplazó por el backend del sistema de tours, conservando el proyecto `lp2_gp2`: paquete `pe.edu.upeu.lp2_gp2`,
   estructura `controller/services/repository/entity/dto/exception/config`, puerto 8082, context-path `/lp2_gp2`,
   Swagger, CORS y el servicio de imágenes. El ZIP también traía el frontend antiguo de e-commerce; aquí se usa el nuevo.
2. **El script no se ejecuta en Oracle tal cual.** `CONSTRAINT chk_pas_fecha_nac CHECK (fecha_nacimiento < SYSDATE)`
   produce **ORA-02436** (Oracle no permite `SYSDATE` en un CHECK). Es el único cambio en `01_schema.sql`: esa línea queda
   comentada y la misma regla se aplica con el trigger `TRG_PAS_FECHA_NAC` (y también en el backend).
3. **El frontend no tenía backend** (guardaba todo en `localStorage`). Cambios en el frontend, sin tocar HTML ni CSS:
   - `TourStore` ahora llama a la API (mismos métodos y señales; las escrituras devuelven `Promise<Resultado>`).
   - `AuthService` hace login real y guarda el token; nuevo `auth.interceptor.ts` añade el token y vuelve al login si vence.
   - `ImagenService` sube la imagen al backend (antes la guardaba en `localStorage`).
   - Componentes: `await` en guardar/eliminar, carga inicial en el shell y recarga directa de `/reservas/:id`.
   - Eliminados `core/data/seed.ts` (pasó a `02_data.sql`), `local-store.ts` y `hash.ts` (el hash ahora es BCrypt en el backend).
4. **Contraseñas:** el frontend usaba SHA-256 sin sal en el navegador. Ahora `password_hash` guarda **BCrypt** y la
   verificación se hace en el servidor; la clave nunca se compara en el navegador.

## 7. Pruebas realizadas

- **Backend** (`./mvnw test`): 10 pruebas de integración — login/401, CRUD de catálogos, duplicados (409), validaciones,
  salidas (capacidad, cruces, cupos), reservas, pagos en PEN/USD, aprobar/rechazar, confirmar/completar/anular con
  reembolso, eliminaciones protegidas, subida y borrado de imágenes, logout.
- **Oracle 23 Free real**: `01_schema.sql` + `02_data.sql` ejecutados sin errores; API probada contra Oracle.
- **Punta a punta** (`pruebas-e2e/e2e.js`): 23 verificaciones en navegador sobre Angular + backend + Oracle.
  Para repetirla: con todo en marcha y la BD recién cargada, `npm i -D playwright && npx playwright install chromium`
  y luego `node pruebas-e2e/e2e.js`.

## 8. Pendientes / decisiones a confirmar

- **Roles:** `ADMIN` y `VENTAS` tienen hoy los mismos permisos (el frontend no los diferencia). Si se necesita, se puede
  restringir por rol en el backend (por ejemplo, que solo `ADMIN` elimine o anule).
- **Sesiones en memoria:** si se reinicia el backend, hay que volver a iniciar sesión (duran 12 h, `tour.sesion-horas`).
- **Tipos de cambio fijos:** `TASAS` (frontend) y `tour.tasas` (backend) deben mantenerse iguales.
- **Producción:** `ng build` genera archivos estáticos que llaman a `/api/v1` y `/lp2_gp2/images` en el mismo origen;
  hay que servirlos detrás de un proxy (Nginx/Apache) que redirija esas rutas al backend.
- La hora de `fecha_reserva` / `fecha_pago` es la del servidor donde corre el backend.
