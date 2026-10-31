package pe.edu.upeu.lp2_gp2.entity;

import jakarta.persistence.*;

/** Tabla TRANSPORTE: Unidad de transporte asignable a una salida. */
@Entity
@Table(name = "TRANSPORTE")
public class Transporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transporte_id")
    private Long transporteId;

    @Column(name = "placa", nullable = false, unique = true, length = 15)
    private String placa;

    @Column(name = "marca_modelo", length = 100)
    private String marcaModelo;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    public Transporte() {
    }

    public Long getTransporteId() {
        return transporteId;
    }

    public void setTransporteId(Long transporteId) {
        this.transporteId = transporteId;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarcaModelo() {
        return marcaModelo;
    }

    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
}
