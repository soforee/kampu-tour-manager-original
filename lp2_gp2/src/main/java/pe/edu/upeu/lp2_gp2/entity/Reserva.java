package pe.edu.upeu.lp2_gp2.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Tabla RESERVAS: Reserva de un cliente sobre una salida programada. */
@Entity
@Table(name = "RESERVAS")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "salida_id", nullable = false)
    private Long salidaId;

    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "numero_viajeros", nullable = false)
    private Integer numeroViajeros;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_pagado", precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "estado_reserva", length = 20)
    private String estadoReserva;

    @Column(name = "estado_pago", length = 20)
    private String estadoPago;

    @Column(name = "observaciones", length = 200)
    private String observaciones;

    @Column(name = "forma_reembolso", length = 50)
    private String formaReembolso;

    @Column(name = "usuario_anula_id")
    private Long usuarioAnulaId;

    public Reserva() {
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getSalidaId() {
        return salidaId;
    }

    public void setSalidaId(Long salidaId) {
        this.salidaId = salidaId;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Integer getNumeroViajeros() {
        return numeroViajeros;
    }

    public void setNumeroViajeros(Integer numeroViajeros) {
        this.numeroViajeros = numeroViajeros;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFormaReembolso() {
        return formaReembolso;
    }

    public void setFormaReembolso(String formaReembolso) {
        this.formaReembolso = formaReembolso;
    }

    public Long getUsuarioAnulaId() {
        return usuarioAnulaId;
    }

    public void setUsuarioAnulaId(Long usuarioAnulaId) {
        this.usuarioAnulaId = usuarioAnulaId;
    }
}
