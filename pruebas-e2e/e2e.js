// E2E: navegador real -> Angular (ng serve + proxy) -> Spring Boot -> Oracle
const { chromium } = require('playwright');
const BASE = 'http://localhost:4200';
const IMG = require('path').join(__dirname, '../frontend_lp2_gp2/public/paisaje_inicio.jpg');
const OUT = process.argv[2] || __dirname;
let paso = 0;
const log = m => console.log(`  ✔ ${++paso}. ${m}`);
const fecha = d => { const x = new Date(); x.setDate(x.getDate() + d); return x.toISOString().slice(0, 10); };

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1400, height: 1000 } });
  // sin Internet aquí: se bloquean CDN de Tailwind / Google Fonts (solo afectan estilos)
  await page.route(u => !u.hostname.startsWith('localhost'), r => r.abort());
  const erroresConsola = [];
  page.on('pageerror', e => { if (!e.message.includes('tailwind is not defined')) erroresConsola.push(e.message); }); // CDN bloqueado en este entorno

  const campo = (texto) => page.locator('label', { hasText: texto }).last().locator('xpath=following-sibling::*[1]');
  const swal = async () => { const p = page.locator('.swal2-popup'); await p.waitFor(); return p; };
  const esperaToast = async (texto) => { await page.locator('.swal2-toast', { hasText: texto }).waitFor(); await page.locator('.swal2-toast').waitFor({ state: 'detached', timeout: 6000 }).catch(() => {}); };
  const esperaError = async (texto) => {
    const p = page.locator('.swal2-popup:not(.swal2-toast)'); await p.waitFor();
    try { await p.locator('.swal2-html-container', { hasText: texto }).waitFor({ timeout: 8000 }); }
    catch { throw new Error(`Se esperaba "${texto}" y llegó "${await p.locator('.swal2-html-container').innerText()}"`); }
    await p.locator('.swal2-confirm').click(); await p.waitFor({ state: 'detached' });
  };
  const respuesta = (frag, metodo) => page.waitForResponse(r => r.url().includes(frag) && r.request().method() === metodo);

  // 1. Login
  await page.goto(BASE + '/dashboard');
  await page.waitForURL('**/login');
  await page.fill('input[name=username]', 'admin');
  await page.fill('input[type=password]', 'malaclave');
  await page.click('button[type=submit]');
  await page.getByText('Usuario o contraseña incorrectos.').waitFor();
  log('login con clave incorrecta muestra el mensaje del backend');
  await page.fill('input[type=password]', 'admin123');
  await page.click('button[type=submit]');
  await page.waitForURL('**/dashboard');
  await page.getByText('Pedro Huertas Vela').first().waitFor(); // reservas recientes desde Oracle
  log('login OK y el panel muestra datos de Oracle');
  await page.screenshot({ path: OUT + '/01-dashboard.png' });

  // 2. Clientes
  await page.goto(BASE + '/clientes');
  await page.getByText('Ana Ramírez Soto').waitFor();
  await page.getByRole('button', { name: 'Nuevo cliente' }).click();
  await campo('Nombre completo').fill('Carmen E2E Prueba');
  await campo('N° de documento').fill('77788899');
  await campo('Correo').fill('carmen@mail.com');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await esperaToast('Cliente registrado');
  await page.getByText('Carmen E2E Prueba').waitFor();
  log('registrar cliente');
  await page.getByRole('button', { name: 'Nuevo cliente' }).click();
  await campo('Nombre completo').fill('Duplicado');
  await campo('N° de documento').fill('77788899');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await esperaError('Ya existe un cliente con ese número de documento.');
  await page.getByRole('button', { name: 'Cancelar' }).click();
  log('documento duplicado -> error 409 del backend mostrado en SweetAlert');
  const fila = page.locator('tr', { hasText: 'Carmen E2E Prueba' });
  await fila.locator('button[title=Editar]').click();
  await campo('Teléfono').fill('911222333');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await esperaToast('Cliente actualizado');
  await page.locator('tr', { hasText: '911222333' }).waitFor();
  log('editar cliente');

  // 3. Guías y transporte
  await page.goto(BASE + '/recursos');
  await page.getByRole('button', { name: 'Nuevo guía' }).click();
  await campo('Nombre completo').fill('Guía E2E');
  await campo('Especialidad').fill('Pruebas');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await esperaToast('Guardado');
  await page.getByText('Guía E2E').waitFor();
  await page.getByRole('button', { name: 'Transporte' }).click();
  await page.getByRole('button', { name: 'Nuevo transporte' }).click();
  await campo('Marca / modelo').fill('Sprinter E2E');
  await campo('Placa').fill('e2e-001');
  await campo('Capacidad').fill('12');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await esperaToast('Guardado');
  await page.getByText('E2E-001').waitFor();
  log('crear guía y transporte (placa normalizada a mayúsculas por el backend)');

  // 4. Paquete con imagen
  await page.goto(BASE + '/paquetes');
  await page.getByRole('button', { name: 'Nuevo paquete' }).click();
  await campo('Nombre').fill('Tour E2E Huaraz');
  await campo('Descripción').fill('Laguna 69 y nevados');
  await campo('Días').fill('3');
  await campo('Precio base').fill('500');
  await page.setInputFiles('input[type=file]', IMG);
  await page.locator('app-modal img').waitFor();
  const subida = respuesta('/api/v1/paquetes/imagen', 'POST');
  await page.getByRole('button', { name: 'Guardar' }).click();
  const rutaImg = (await (await subida).json()).imagen;
  await esperaToast('Paquete creado');
  const tarjeta = page.locator('article', { hasText: 'Tour E2E Huaraz' });
  const img = tarjeta.locator('img');
  await img.waitFor();
  await page.waitForFunction(el => el.complete && el.naturalWidth > 0, await img.elementHandle());
  log(`paquete con imagen subida al backend (${rutaImg}) y mostrada desde /lp2_gp2/images`);
  await page.screenshot({ path: OUT + '/02-paquetes.png' });

  // 5. Salida programada
  await page.goto(BASE + '/salidas');
  await page.getByRole('button', { name: 'Nueva salida' }).click();
  await campo('Paquete').selectOption({ label: 'Tour E2E Huaraz (3D / 2N)' });
  await campo('Fecha inicio').fill(fecha(40));
  await campo('Fecha inicio').dispatchEvent('change');
  await campo('Cupos máximos').fill('20');
  await campo('Guía').selectOption({ label: 'Guía E2E' });
  await campo('Transporte').selectOption({ label: 'Sprinter E2E · E2E-001 (12)' });
  await page.getByRole('button', { name: 'Guardar' }).click();
  await esperaError('Los cupos superan la capacidad del transporte (12).');
  log('validación de capacidad del transporte (backend)');
  await campo('Cupos máximos').fill('10');
  const creada = respuesta('/api/v1/salidas', 'POST');
  await page.getByRole('button', { name: 'Guardar' }).click();
  const salida = await (await creada).json();
  await esperaToast('Salida creada');
  if (salida.fechaFin !== fecha(42)) throw new Error('fechaFin sugerida incorrecta: ' + salida.fechaFin);
  log(`crear salida #${salida.salidaId} (${salida.fechaInicio} → ${salida.fechaFin}, 10 cupos)`);

  // 6. Reserva nueva
  await page.goto(BASE + `/reservas/nueva?salida=${salida.salidaId}`);
  await campo('Cliente').selectOption({ label: 'Carmen E2E Prueba · DNI 77788899' }).catch(async () => {
    const opt = await campo('Cliente').locator('option', { hasText: 'Carmen E2E Prueba' }).getAttribute('value');
    await campo('Cliente').selectOption(opt);
  });
  await page.getByRole('button', { name: 'Usar datos del cliente' }).click();
  await page.getByRole('button', { name: 'Agregar' }).click();
  await page.locator('label', { hasText: 'Nombre completo' }).nth(1).locator('xpath=following-sibling::*[1]').fill('Pedro E2E');
  await page.locator('label', { hasText: 'N° de documento' }).nth(1).locator('xpath=following-sibling::*[1]').fill('77788899');
  await page.getByRole('button', { name: 'Crear reserva' }).click();
  await esperaError('El documento 77788899 está repetido en la reserva.');
  log('documento repetido en la reserva -> error del backend');
  await page.locator('label', { hasText: 'N° de documento' }).nth(1).locator('xpath=following-sibling::*[1]').fill('11223344');
  await page.locator('label', { hasText: 'Fecha de nacimiento' }).nth(1).locator('xpath=following-sibling::*[1]').fill('2001-05-20');
  await page.getByRole('button', { name: 'Crear reserva' }).click();
  await esperaToast('Reserva creada');
  await page.waitForURL(/\/reservas\/\d+$/);
  const reservaId = Number(page.url().split('/').pop());
  await page.getByText('S/ 1,000.00').first().waitFor();
  log(`crear reserva R-${String(reservaId).padStart(4, '0')} con 2 pasajeros (monto total S/ 1,000.00)`);

  // recarga directa del detalle (deep link) -> debe cargar desde la API
  await page.reload();
  await page.getByText('Pedro E2E').waitFor();
  log('recarga directa de /reservas/:id carga los datos del backend');

  // 7. Pagos
  await campo('Monto').fill('400');
  await page.getByRole('button', { name: 'Registrar' }).click();
  await esperaToast('Pago registrado');
  await page.getByText('PARCIAL').first().waitFor();
  log('pago APROBADO S/ 400 -> estado de pago PARCIAL');
  await campo('Monto').fill('200');
  await campo('Moneda').selectOption('USD');
  await campo('Estado').selectOption('PENDIENTE');
  await page.getByRole('button', { name: 'Registrar' }).click();
  await esperaToast('Pago registrado');
  await page.locator('button[title=Aprobar]').click();
  await esperaError('Aprobar este pago excedería el saldo de la reserva.');
  log('aprobar USD 200 (= S/ 750) excede el saldo -> rechazado por el backend');
  await page.locator('button[title=Rechazar]').click();
  await esperaToast('Pago rechazado');
  await campo('Moneda').selectOption('USD');
  await page.getByRole('button', { name: 'Usar saldo' }).click();
  await page.getByRole('button', { name: 'Registrar' }).click();
  await esperaToast('Pago registrado');
  await page.getByText('PAGADO').first().waitFor();
  log('pago del saldo en USD (conversión con tasas del backend) -> PAGADO');

  // 8. Confirmar y anular con reembolso
  await page.getByRole('button', { name: 'Confirmar' }).click();
  await (await swal()).locator('.swal2-confirm').click();
  await esperaToast('Reserva confirmada');
  await page.getByRole('button', { name: 'Anular' }).click();
  const dlg = await swal();
  await dlg.locator('select').selectOption('Yape');
  await dlg.locator('.swal2-confirm').click();
  await esperaToast('Reserva anulada');
  await page.getByText('ANULADA').first().waitFor();
  await page.getByText('REEMBOLSADO').first().waitFor();
  await page.getByText('admin').first().waitFor();
  log('confirmar y anular con reembolso (forma Yape, usuario_anula_id = admin)');
  await page.screenshot({ path: OUT + '/03-reserva-anulada.png', fullPage: true });

  const salidaTrasAnular = await page.evaluate(async id => {
    const t = JSON.parse(localStorage.getItem('tm3_session')).token;
    return (await fetch('/api/v1/salidas/' + id, { headers: { Authorization: 'Bearer ' + t } })).json();
  }, salida.salidaId);
  if (salidaTrasAnular.cuposDisponibles !== 10) throw new Error('cupos no liberados: ' + salidaTrasAnular.cuposDisponibles);
  log('los cupos de la salida volvieron a 10 tras anular');

  // 9. Pantalla de pagos
  await page.goto(BASE + '/pagos');
  await page.locator('tr', { hasText: 'Carmen E2E Prueba' }).first().waitFor();
  log('la pantalla Pagos lista los pagos de la reserva (reembolsados/rechazado)');

  // 10. Eliminaciones protegidas y permitidas
  await page.goto(BASE + '/clientes');
  await page.locator('tr', { hasText: 'Carmen E2E Prueba' }).locator('button[title=Eliminar]').click();
  await (await swal()).locator('.swal2-confirm').click();
  await esperaError('El cliente tiene reservas registradas.');
  log('no se elimina un cliente con reservas');
  await page.goto(BASE + '/recursos');
  await page.locator('div.card', { hasText: 'Guía E2E' }).locator('.icon-btn-danger').click();
  await (await swal()).locator('.swal2-confirm').click();
  await esperaError('El guía está asignado a salidas programadas.');
  log('no se elimina un guía asignado a salidas');

  // 11. Sesión: token inválido -> vuelve al login; logout
  await page.evaluate(() => { const s = JSON.parse(localStorage.getItem('tm3_session')); s.token = 'invalido'; localStorage.setItem('tm3_session', JSON.stringify(s)); });
  await page.goto(BASE + '/dashboard');
  await page.waitForURL('**/login');
  log('token vencido/ inválido -> 401 -> redirige al login');
  await page.fill('input[name=username]', 'ventas');
  await page.fill('input[type=password]', 'ventas123');
  await page.click('button[type=submit]');
  await page.waitForURL('**/dashboard');
  await page.getByText('VENTAS').first().waitFor();
  log('login con el usuario ventas');

  if (erroresConsola.length) throw new Error('Errores JS en la página: ' + erroresConsola.join(' | '));
  log('sin errores de JavaScript en la consola');
  await browser.close();
  console.log(`\nE2E OK: ${paso} verificaciones`);
})().catch(async e => { console.error('\n  ✘ FALLÓ en el paso', paso + 1, ':', e.message); process.exit(1); });
