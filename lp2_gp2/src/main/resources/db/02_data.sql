-- =====================================================================
-- KAMPU Tour Manager - Datos de demostración (los mismos del frontend: core/data/seed.ts)
-- Ejecutar DESPUÉS de 01_schema.sql, sobre tablas vacías.
-- Login: admin / admin123  ·  ventas / ventas123
-- =====================================================================
-- USUARIOS_SISTEMA (password_hash = BCrypt de admin123 / ventas123)
INSERT INTO USUARIOS_SISTEMA (usuario_id, username, password_hash, rol, estado) VALUES (1, 'admin', '$2a$10$epXHjYOSfrk9GZbVDz/NJOQUuYxElobpkWFwvsJKcMpAn4S2LGyKy', 'ADMIN', 'ACTIVO');
INSERT INTO USUARIOS_SISTEMA (usuario_id, username, password_hash, rol, estado) VALUES (2, 'ventas', '$2a$10$hwbumrqX9mU0hd9tX5mRfOusJn5olR9VtNxwaSYcSPlNdQV3NBgJ2', 'VENTAS', 'ACTIVO');

-- CLIENTES
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (1, 'Ana Ramírez Soto', 'DNI', '45871236', 'ana.ramirez@mail.com', '987654321');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (2, 'Lucía Fernández Cruz', 'DNI', '71452368', 'lucia.fernandez@mail.com', '956874123');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (3, 'John Miller', 'PASAPORTE', 'X4587126', 'john.miller@mail.com', '+13055550142');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (4, 'Sofía Castillo Rojas', 'CE', '001245789', 'sofia.castillo@mail.com', '945123789');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (5, 'Diego Salazar Luna', 'DNI', '43698521', 'diego.salazar@mail.com', '965478123');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (6, 'Pedro Huertas Vela', 'DNI', '42587136', 'pedro.huertas@mail.com', '934521876');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (7, 'Luis Paredes Gil', 'DNI', '47125836', 'luis.paredes@mail.com', '912008745');
INSERT INTO CLIENTES (cliente_id, nombre_completo, tipo_documento, numero_documento, email, telefono) VALUES (8, 'Andrea Campos Ruiz', 'DNI', '70418529', 'andrea.campos@mail.com', '999120334');

-- GUIAS
INSERT INTO GUIAS (guia_id, nombre_completo, especialidad, telefono) VALUES (1, 'Carlos Quispe', 'Cultura e historia andina', '984112233');
INSERT INTO GUIAS (guia_id, nombre_completo, especialidad, telefono) VALUES (2, 'María Torres', 'Gastronomía y city tours', '991445120');
INSERT INTO GUIAS (guia_id, nombre_completo, especialidad, telefono) VALUES (3, 'Luis Valdivia', 'Aventura y trekking', '958302771');
INSERT INTO GUIAS (guia_id, nombre_completo, especialidad, telefono) VALUES (4, 'Rosa Huamán', 'Naturaleza y selva', '976880415');

-- TRANSPORTE
INSERT INTO TRANSPORTE (transporte_id, placa, marca_modelo, capacidad) VALUES (1, 'BUS-401', 'Mercedes-Benz O500 (bus turístico)', 40);
INSERT INTO TRANSPORTE (transporte_id, placa, marca_modelo, capacidad) VALUES (2, 'VAN-152', 'Toyota Hiace (minivan)', 15);
INSERT INTO TRANSPORTE (transporte_id, placa, marca_modelo, capacidad) VALUES (3, 'BUS-302', 'Volvo B7R (bus turístico)', 30);
INSERT INTO TRANSPORTE (transporte_id, placa, marca_modelo, capacidad) VALUES (4, 'CAM-084', 'Toyota Hilux 4x4 (camioneta)', 8);

-- PAQUETES_TOUR (imagen vacía: se sube desde la pantalla Paquetes)
INSERT INTO PAQUETES_TOUR (paquete_id, nombre, descripcion, precio_base, dias, noches, imagen, estado) VALUES (1, 'Machu Picchu Clásico', 'Valle Sagrado, tren a Aguas Calientes y visita guiada a la ciudadela.', 1850, 4, 3, NULL, 'ACTIVO');
INSERT INTO PAQUETES_TOUR (paquete_id, nombre, descripcion, precio_base, dias, noches, imagen, estado) VALUES (2, 'Lima Gastronómica', 'Centro histórico, mercados, ceviche y degustación en Barranco.', 420, 2, 1, NULL, 'ACTIVO');
INSERT INTO PAQUETES_TOUR (paquete_id, nombre, descripcion, precio_base, dias, noches, imagen, estado) VALUES (3, 'Arequipa y Cañón del Colca', 'Ciudad blanca, Cruz del Cóndor y termas de Chivay.', 980, 3, 2, NULL, 'ACTIVO');
INSERT INTO PAQUETES_TOUR (paquete_id, nombre, descripcion, precio_base, dias, noches, imagen, estado) VALUES (4, 'Paracas e Ica Aventura', 'Islas Ballestas, reserva de Paracas, Huacachina y sandboard.', 380, 2, 1, NULL, 'ACTIVO');
INSERT INTO PAQUETES_TOUR (paquete_id, nombre, descripcion, precio_base, dias, noches, imagen, estado) VALUES (5, 'Amazonía Tambopata', 'Lodge en la selva, lago Sandoval y avistamiento de fauna.', 1650, 4, 3, NULL, 'ACTIVO');
INSERT INTO PAQUETES_TOUR (paquete_id, nombre, descripcion, precio_base, dias, noches, imagen, estado) VALUES (6, 'Lago Titicaca y Puno', 'Islas flotantes de los Uros, Taquile y Sillustani.', 890, 3, 2, NULL, 'INACTIVO');

-- SALIDAS_PROGRAMADAS
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (1, 1, DATE '2026-11-05', DATE '2026-11-08', 20, 17, 1, 1, 'CONFIRMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (2, 1, DATE '2026-12-10', DATE '2026-12-13', 20, 20, 1, 1, 'PROGRAMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (3, 2, DATE '2026-11-14', DATE '2026-11-15', 15, 12, 2, 2, 'PROGRAMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (4, 3, DATE '2026-11-20', DATE '2026-11-22', 16, 16, 3, 3, 'PROGRAMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (5, 4, DATE '2026-11-07', DATE '2026-11-08', 25, 25, 2, 3, 'PROGRAMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (6, 5, DATE '2026-12-03', DATE '2026-12-06', 12, 10, 4, 2, 'PROGRAMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (7, 1, DATE '2026-10-08', DATE '2026-10-11', 20, 18, 1, 1, 'COMPLETADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (8, 3, DATE '2026-10-21', DATE '2026-10-23', 16, 13, 3, 3, 'CONFIRMADA');
INSERT INTO SALIDAS_PROGRAMADAS (salida_id, paquete_id, fecha_inicio, fecha_fin, cupos_maximos, cupos_disponibles, guia_id, transporte_id, estado) VALUES (9, 6, DATE '2027-01-15', DATE '2027-01-17', 18, 18, 4, 3, 'CANCELADA');

-- RESERVAS
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (1, 1, 7, TIMESTAMP '2026-09-12 15:30:00', 2, 3700, 3700, 'COMPLETADA', 'PAGADO', NULL, NULL, NULL);
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (2, 2, 8, TIMESTAMP '2026-09-30 10:05:00', 3, 2940, 2940, 'CONFIRMADA', 'PAGADO', 'Un menor de edad en el grupo.', NULL, NULL);
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (3, 3, 1, TIMESTAMP '2026-10-02 18:40:00', 2, 3700, 1850, 'CONFIRMADA', 'PARCIAL', 'Habitación matrimonial.', NULL, NULL);
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (4, 4, 1, TIMESTAMP '2026-10-15 09:12:00', 1, 1850, 0, 'PENDIENTE', 'PENDIENTE', NULL, NULL, NULL);
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (5, 5, 3, TIMESTAMP '2026-10-10 14:20:00', 3, 1260, 630, 'CONFIRMADA', 'PARCIAL', NULL, NULL, NULL);
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (6, 6, 6, TIMESTAMP '2026-10-18 16:55:00', 2, 3300, 990, 'PENDIENTE', 'PARCIAL', 'Dieta vegetariana.', NULL, NULL);
INSERT INTO RESERVAS (reserva_id, cliente_id, salida_id, fecha_reserva, numero_viajeros, monto_total, monto_pagado, estado_reserva, estado_pago, observaciones, forma_reembolso, usuario_anula_id) VALUES (7, 7, 4, TIMESTAMP '2026-10-05 11:00:00', 1, 980, 0, 'ANULADA', 'REEMBOLSADO', 'Anulada a pedido del cliente.', 'Yape', 1);

-- PASAJEROS_RESERVA
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (1, 1, 'Ana Ramírez Soto', 'DNI', '45871236', 'Peruana', DATE '1990-04-12');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (2, 1, 'Jorge Mendoza Paredes', 'DNI', '40125879', 'Peruana', DATE '1985-09-30');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (3, 2, 'Lucía Fernández Cruz', 'DNI', '71452368', 'Peruana', DATE '1995-01-22');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (4, 2, 'Mateo Fernández Cruz', 'DNI', '71452369', 'Peruana', DATE '2012-06-15');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (5, 2, 'Carla Vega Ruiz', 'DNI', '46325871', 'Peruana', DATE '1992-11-03');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (6, 3, 'John Miller', 'PASAPORTE', 'X4587126', 'Estadounidense', DATE '1980-02-18');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (7, 3, 'Emily Miller', 'PASAPORTE', 'X4587127', 'Estadounidense', DATE '1983-07-09');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (8, 4, 'Sofía Castillo Rojas', 'CE', '001245789', 'Colombiana', DATE '1998-12-25');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (9, 5, 'Diego Salazar Luna', 'DNI', '43698521', 'Peruana', DATE '1988-03-07');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (10, 5, 'Valeria Salazar Luna', 'DNI', '43698522', 'Peruana', DATE '1991-08-19');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (11, 5, 'Camila Salazar Luna', 'DNI', '43698523', 'Peruana', DATE '2015-02-11');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (12, 6, 'Pedro Huertas Vela', 'DNI', '42587136', 'Peruana', DATE '1979-05-30');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (13, 6, 'Marta Huertas Vela', 'DNI', '42587137', 'Peruana', DATE '1982-10-14');
INSERT INTO PASAJEROS_RESERVA (pasajero_id, reserva_id, nombre_completo, tipo_documento, numero_documento, nacionalidad, fecha_nacimiento) VALUES (14, 7, 'Luis Paredes Gil', 'DNI', '47125836', 'Peruana', DATE '1986-01-09');

-- PAGOS
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (1, 1, 1850, 'PEN', 'Transferencia', 'OP-883421', TIMESTAMP '2026-09-12 15:45:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (2, 1, 1850, 'PEN', 'Tarjeta', 'VISA-1127', TIMESTAMP '2026-09-28 12:10:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (3, 2, 1470, 'PEN', 'Yape', 'YP-55120', TIMESTAMP '2026-09-30 10:20:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (4, 2, 1470, 'PEN', 'Efectivo', 'EF-00012', TIMESTAMP '2026-10-12 09:00:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (5, 3, 1850, 'PEN', 'Tarjeta', 'MC-9034', TIMESTAMP '2026-10-02 18:50:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (6, 4, 300, 'PEN', 'Tarjeta', 'VISA-7781', TIMESTAMP '2026-10-15 09:20:00', 'RECHAZADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (7, 4, 925, 'PEN', 'Transferencia', 'OP-901122', TIMESTAMP '2026-10-21 17:30:00', 'PENDIENTE');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (8, 5, 630, 'PEN', 'Efectivo', 'EF-00013', TIMESTAMP '2026-10-10 14:30:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (9, 6, 264, 'USD', 'Transferencia', 'OP-902277', TIMESTAMP '2026-10-18 17:05:00', 'APROBADO');
INSERT INTO PAGOS (pago_id, reserva_id, monto, moneda, metodo_pago, codigo_transaccion, fecha_pago, estado_transaccion) VALUES (10, 7, 300, 'PEN', 'Yape', 'YP-54001', TIMESTAMP '2026-10-05 11:10:00', 'REEMBOLSADO');

COMMIT;

-- Las filas anteriores usan IDs explícitos; se reinician las identidades para que
-- los siguientes INSERT del backend continúen después del último ID cargado.
ALTER TABLE USUARIOS_SISTEMA    MODIFY usuario_id    GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE CLIENTES            MODIFY cliente_id    GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE GUIAS               MODIFY guia_id       GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE TRANSPORTE          MODIFY transporte_id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE PAQUETES_TOUR       MODIFY paquete_id    GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE SALIDAS_PROGRAMADAS MODIFY salida_id     GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE RESERVAS            MODIFY reserva_id    GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE PASAJEROS_RESERVA   MODIFY pasajero_id   GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
ALTER TABLE PAGOS               MODIFY pago_id       GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE);
