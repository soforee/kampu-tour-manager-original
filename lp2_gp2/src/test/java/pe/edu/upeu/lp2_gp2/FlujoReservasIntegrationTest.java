package pe.edu.upeu.lp2_gp2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de integración de punta a punta sobre H2 (modo Oracle) con el mismo script de tablas:
 * login, CRUD de catálogos, salidas, reservas, pagos y anulación, con las reglas del frontend.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlujoReservasIntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;

    static String token;
    static long guiaId, transporteId, paqueteId, clienteId, salidaId, reservaId, pagoPendienteId, adminId;

    // ---------- utilidades ----------
    private MockHttpServletRequestBuilder conToken(MockHttpServletRequestBuilder b) {
        return b.header("Authorization", "Bearer " + token);
    }

    private ResultActions enviar(MockHttpServletRequestBuilder b, Object cuerpo) throws Exception {
        return mvc.perform(conToken(b).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(cuerpo)));
    }

    private JsonNode leer(ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }

    private static Map<String, Object> pasajero(String nombre, String doc) {
        return Map.of("nombreCompleto", nombre, "tipoDocumento", "DNI", "numeroDocumento", doc,
                "nacionalidad", "Peruana", "fechaNacimiento", "1990-01-01");
    }

    // ---------- autenticación ----------
    @Test
    @Order(1)
    void loginYProteccionDeLaApi() throws Exception {
        mvc.perform(get("/api/v1/clientes")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(containsString("sesión")));
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Usuario o contraseña incorrectos."));

        JsonNode r = leer(mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ADMIN\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.username").value("admin"))
                .andExpect(jsonPath("$.usuario.rol").value("ADMIN"))
                .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist()));
        token = r.get("token").asText();
        adminId = r.get("usuario").get("usuarioId").asLong();

        mvc.perform(conToken(get("/api/v1/usuarios"))).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username", hasItems("admin", "ventas")));
    }

    // ---------- catálogos ----------
    @Test
    @Order(2)
    void catalogos() throws Exception {
        guiaId = leer(enviar(post("/api/v1/guias"), Map.of("nombreCompleto", " Carlos Quispe ", "especialidad", "", "telefono", "984112233"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCompleto").value("Carlos Quispe"))
                .andExpect(jsonPath("$.especialidad").value(""))) // NULL en BD -> "" en JSON
                .get("guiaId").asLong();

        transporteId = leer(enviar(post("/api/v1/transportes"), Map.of("placa", "van-152", "marcaModelo", "Toyota Hiace", "capacidad", 10))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("VAN-152"))).get("transporteId").asLong();
        enviar(post("/api/v1/transportes"), Map.of("placa", "VAN-152", "marcaModelo", "Otro", "capacidad", 5))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe un transporte con esa placa."));
        enviar(post("/api/v1/transportes"), Map.of("placa", "X", "capacidad", 1000))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La capacidad debe estar entre 1 y 999."));

        paqueteId = leer(enviar(post("/api/v1/paquetes"), Map.of("nombre", "Lima Gastronómica", "descripcion", "Centro y mercados",
                "precioBase", 100, "dias", 2, "noches", 1, "imagen", "", "estado", "ACTIVO"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.precioBase").value(100.0))
                .andExpect(jsonPath("$.imagen").value(""))).get("paqueteId").asLong();
        enviar(post("/api/v1/paquetes"), Map.of("nombre", "Sin días", "precioBase", 10, "dias", 0, "noches", 0, "estado", "ACTIVO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Los días deben estar entre 1 y 99."));

        clienteId = leer(enviar(post("/api/v1/clientes"), Map.of("nombreCompleto", "Ana Ramírez", "tipoDocumento", "DNI",
                "numeroDocumento", "45871236", "email", "", "telefono", "987654321"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(""))).get("clienteId").asLong();
        enviar(post("/api/v1/clientes"), Map.of("nombreCompleto", "Otra", "tipoDocumento", "DNI", "numeroDocumento", "45871236"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe un cliente con ese número de documento."));
        enviar(put("/api/v1/clientes/" + clienteId), Map.of("nombreCompleto", "Ana Ramírez Soto", "tipoDocumento", "DNI",
                "numeroDocumento", "45871236", "email", "ana@mail.com", "telefono", "987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Ana Ramírez Soto"));
        mvc.perform(conToken(get("/api/v1/clientes/999999"))).andExpect(status().isNotFound());
    }

    // ---------- salidas ----------
    @Test
    @Order(3)
    void salidas() throws Exception {
        String ini = LocalDate.now().plusDays(30).toString(), fin = LocalDate.now().plusDays(31).toString();
        Map<String, Object> base = new java.util.HashMap<>(Map.of("paqueteId", paqueteId, "fechaInicio", ini, "fechaFin", fin,
                "cuposMaximos", 12, "guiaId", guiaId, "transporteId", transporteId, "estado", "PROGRAMADA"));
        enviar(post("/api/v1/salidas"), base).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Los cupos superan la capacidad del transporte (10)."));

        base.put("cuposMaximos", 10);
        base.put("fechaFin", LocalDate.now().plusDays(29).toString());
        enviar(post("/api/v1/salidas"), base).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La fecha fin no puede ser anterior a la fecha inicio."));

        base.put("fechaFin", fin);
        salidaId = leer(enviar(post("/api/v1/salidas"), base).andExpect(status().isCreated())
                .andExpect(jsonPath("$.cuposDisponibles").value(10))
                .andExpect(jsonPath("$.fechaInicio").value(ini))).get("salidaId").asLong();

        // mismo guía y fechas cruzadas -> choque
        Map<String, Object> otra = new java.util.HashMap<>(base);
        otra.put("transporteId", null);
        enviar(post("/api/v1/salidas"), otra).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El guía ya está asignado a otra salida en esas fechas."));
        // una salida CANCELADA no bloquea al guía
        otra.put("estado", "CANCELADA");
        long cancelada = leer(enviar(post("/api/v1/salidas"), otra).andExpect(status().isCreated())).get("salidaId").asLong();
        mvc.perform(conToken(delete("/api/v1/salidas/" + cancelada))).andExpect(status().isNoContent());
        // editar la propia salida no choca consigo misma
        enviar(put("/api/v1/salidas/" + salidaId), base).andExpect(status().isOk());
    }

    // ---------- reservas ----------
    @Test
    @Order(4)
    void reservas() throws Exception {
        Map<String, Object> dto = Map.of("clienteId", clienteId, "salidaId", salidaId, "observaciones", "",
                "pasajeros", List.of(pasajero("Ana Ramírez Soto", "45871236"), pasajero("Jorge Mendoza", "45871236")));
        enviar(post("/api/v1/reservas"), dto).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El documento 45871236 está repetido en la reserva."));

        Map<String, Object> futuro = new java.util.HashMap<>(pasajero("Bebé", "1"));
        futuro.put("fechaNacimiento", LocalDate.now().toString());
        enviar(post("/api/v1/reservas"), Map.of("clienteId", clienteId, "salidaId", salidaId, "pasajeros", List.of(futuro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La fecha de nacimiento de Bebé debe ser anterior a hoy."));

        List<Map<String, Object>> once = new java.util.ArrayList<>();
        for (int i = 0; i < 11; i++) once.add(pasajero("P" + i, "D" + i));
        enviar(post("/api/v1/reservas"), Map.of("clienteId", clienteId, "salidaId", salidaId, "pasajeros", once))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo quedan 10 cupos en esta salida."));

        Map<String, Object> sinFecha = new java.util.HashMap<>(pasajero("Jorge Mendoza", "40125879"));
        sinFecha.put("fechaNacimiento", null);
        reservaId = leer(enviar(post("/api/v1/reservas"), Map.of("clienteId", clienteId, "salidaId", salidaId,
                "observaciones", "Dieta vegetariana", "pasajeros", List.of(pasajero("Ana Ramírez Soto", "45871236"), sinFecha)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroViajeros").value(2))
                .andExpect(jsonPath("$.montoTotal").value(200.0))
                .andExpect(jsonPath("$.montoPagado").value(0))
                .andExpect(jsonPath("$.estadoReserva").value("PENDIENTE"))
                .andExpect(jsonPath("$.estadoPago").value("PENDIENTE"))).get("reservaId").asLong();

        mvc.perform(conToken(get("/api/v1/salidas/" + salidaId))).andExpect(jsonPath("$.cuposDisponibles").value(8));
        mvc.perform(conToken(get("/api/v1/pasajeros").param("reservaId", String.valueOf(reservaId))))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].fechaNacimiento").value(nullValue()));
        // cupos < ocupados
        enviar(put("/api/v1/salidas/" + salidaId), Map.of("paqueteId", paqueteId, "fechaInicio", LocalDate.now().plusDays(30).toString(),
                "fechaFin", LocalDate.now().plusDays(31).toString(), "cuposMaximos", 1, "estado", "PROGRAMADA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya hay 2 viajeros reservados; los cupos no pueden ser menores."));
        enviar(put("/api/v1/salidas/" + salidaId), Map.of("paqueteId", paqueteId, "fechaInicio", LocalDate.now().plusDays(30).toString(),
                "fechaFin", LocalDate.now().plusDays(31).toString(), "cuposMaximos", 10, "estado", "CANCELADA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No puedes cancelar una salida con reservas activas. Anúlalas primero."));
    }

    // ---------- pagos ----------
    @Test
    @Order(5)
    void pagos() throws Exception {
        Map<String, Object> excede = Map.of("reservaId", reservaId, "monto", 300, "moneda", "PEN", "metodoPago", "Efectivo",
                "codigoTransaccion", "", "estadoTransaccion", "APROBADO");
        enviar(post("/api/v1/pagos"), excede).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El pago excede el saldo pendiente (S/ 200.00)."));

        enviar(post("/api/v1/pagos"), Map.of("reservaId", reservaId, "monto", 100, "moneda", "PEN", "metodoPago", "Yape",
                "codigoTransaccion", "", "estadoTransaccion", "APROBADO"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoTransaccion").value(startsWith("TRX-")));
        mvc.perform(conToken(get("/api/v1/reservas/" + reservaId)))
                .andExpect(jsonPath("$.montoPagado").value(100.0))
                .andExpect(jsonPath("$.estadoPago").value("PARCIAL"));

        // 20 USD PENDIENTE (= S/ 75.00) y luego se aprueba
        pagoPendienteId = leer(enviar(post("/api/v1/pagos"), Map.of("reservaId", reservaId, "monto", 20, "moneda", "USD",
                "metodoPago", "Transferencia", "codigoTransaccion", "OP-1", "estadoTransaccion", "PENDIENTE"))
                .andExpect(status().isCreated())).get("pagoId").asLong();
        enviar(patch("/api/v1/pagos/" + pagoPendienteId + "/resolver"), Map.of("estado", "APROBADO"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estadoTransaccion").value("APROBADO"));
        enviar(patch("/api/v1/pagos/" + pagoPendienteId + "/resolver"), Map.of("estado", "RECHAZADO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se resuelven pagos pendientes."));
        mvc.perform(conToken(get("/api/v1/reservas/" + reservaId))).andExpect(jsonPath("$.montoPagado").value(175.0));

        enviar(post("/api/v1/pagos"), Map.of("reservaId", reservaId, "monto", 25, "moneda", "PEN", "metodoPago", "Efectivo",
                "codigoTransaccion", "EF-1", "estadoTransaccion", "APROBADO")).andExpect(status().isCreated());
        mvc.perform(conToken(get("/api/v1/reservas/" + reservaId)))
                .andExpect(jsonPath("$.montoPagado").value(200.0))
                .andExpect(jsonPath("$.estadoPago").value("PAGADO"));
        mvc.perform(conToken(get("/api/v1/pagos"))).andExpect(jsonPath("$", hasSize(3)));
    }

    // ---------- estados y anulación ----------
    @Test
    @Order(6)
    void confirmarYAnular() throws Exception {
        mvc.perform(conToken(patch("/api/v1/reservas/" + reservaId + "/completar"))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se completan reservas confirmadas."));
        mvc.perform(conToken(patch("/api/v1/reservas/" + reservaId + "/confirmar"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoReserva").value("CONFIRMADA"));

        enviar(patch("/api/v1/reservas/" + reservaId + "/anular"), Map.of()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Indica la forma de reembolso."));
        enviar(patch("/api/v1/reservas/" + reservaId + "/anular"), Map.of("formaReembolso", "Yape"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoReserva").value("ANULADA"))
                .andExpect(jsonPath("$.estadoPago").value("REEMBOLSADO"))
                .andExpect(jsonPath("$.montoPagado").value(0))
                .andExpect(jsonPath("$.formaReembolso").value("Yape"))
                .andExpect(jsonPath("$.usuarioAnulaId").value((int) adminId));

        mvc.perform(conToken(get("/api/v1/pagos").param("reservaId", String.valueOf(reservaId))))
                .andExpect(jsonPath("$[*].estadoTransaccion", everyItem(is("REEMBOLSADO"))));
        mvc.perform(conToken(get("/api/v1/salidas/" + salidaId))).andExpect(jsonPath("$.cuposDisponibles").value(10));
        enviar(post("/api/v1/pagos"), Map.of("reservaId", reservaId, "monto", 10, "moneda", "PEN", "estadoTransaccion", "APROBADO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La reserva no admite pagos."));
    }

    // ---------- integridad al eliminar ----------
    @Test
    @Order(7)
    void eliminacionesProtegidas() throws Exception {
        mvc.perform(conToken(delete("/api/v1/clientes/" + clienteId))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El cliente tiene reservas registradas."));
        mvc.perform(conToken(delete("/api/v1/salidas/" + salidaId))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La salida tiene reservas asociadas. Puedes marcarla como CANCELADA."));
        mvc.perform(conToken(delete("/api/v1/guias/" + guiaId))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El guía está asignado a salidas programadas."));
        mvc.perform(conToken(delete("/api/v1/transportes/" + transporteId))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El transporte está asignado a salidas programadas."));
        mvc.perform(conToken(delete("/api/v1/paquetes/" + paqueteId))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El paquete tiene salidas programadas. Desactívalo en lugar de eliminarlo."));
        // ya sin reservas activas, la salida sí puede cancelarse
        enviar(put("/api/v1/salidas/" + salidaId), Map.of("paqueteId", paqueteId, "fechaInicio", LocalDate.now().plusDays(30).toString(),
                "fechaFin", LocalDate.now().plusDays(31).toString(), "cuposMaximos", 10, "estado", "CANCELADA"))
                .andExpect(status().isOk());
        long libre = leer(enviar(post("/api/v1/guias"), Map.of("nombreCompleto", "Libre"))).get("guiaId").asLong();
        mvc.perform(conToken(delete("/api/v1/guias/" + libre))).andExpect(status().isNoContent());
    }

    // ---------- imagen del paquete ----------
    @Test
    @Order(8)
    void imagenDelPaquete() throws Exception {
        MockMultipartFile txt = new MockMultipartFile("file", "a.txt", "text/plain", "hola".getBytes());
        mvc.perform(conToken(multipart("/api/v1/paquetes/imagen").file(txt))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El archivo debe ser una imagen (JPG, PNG o WebP)."));

        MockMultipartFile jpg = new MockMultipartFile("file", "foto.jpg", "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, 1, 2});
        String ruta = leer(mvc.perform(conToken(multipart("/api/v1/paquetes/imagen").file(jpg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imagen").value(matchesPattern("paquetes/[0-9a-f-]+\\.jpg")))).get("imagen").asText();
        Path archivo = Path.of("target/test-images").resolve(ruta);
        assertThat(Files.exists(archivo)).isTrue();
        mvc.perform(get("/images/" + ruta)).andExpect(status().isOk()); // pública para <img>

        Map<String, Object> p = new java.util.HashMap<>(Map.of("nombre", "Lima Gastronómica", "descripcion", "",
                "precioBase", 120, "dias", 2, "noches", 1, "imagen", ruta, "estado", "INACTIVO"));
        enviar(put("/api/v1/paquetes/" + paqueteId), p).andExpect(status().isOk())
                .andExpect(jsonPath("$.imagen").value(ruta))
                .andExpect(jsonPath("$.estado").value("INACTIVO"));
        p.put("imagen", "");
        enviar(put("/api/v1/paquetes/" + paqueteId), p).andExpect(status().isOk());
        assertThat(Files.exists(archivo)).as("al quitar la imagen se borra el archivo").isFalse();
    }

    @Test
    @Order(9)
    void logoutInvalidaElToken() throws Exception {
        mvc.perform(conToken(post("/api/v1/auth/logout"))).andExpect(status().isNoContent());
        mvc.perform(conToken(get("/api/v1/clientes"))).andExpect(status().isUnauthorized());
    }
}
