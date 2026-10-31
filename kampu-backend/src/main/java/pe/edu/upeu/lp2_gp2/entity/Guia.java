package pe.edu.upeu.lp2_gp2.entity;

import jakarta.persistence.*;

/** Tabla GUIAS: Guía turístico asignable a una salida. */
@Entity
@Table(name = "GUIAS")
public class Guia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guia_id")
    private Long guiaId;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "telefono", length = 20)
    private String telefono;

    public Guia() {
    }

    public Long getGuiaId() {
        return guiaId;
    }

    public void setGuiaId(Long guiaId) {
        this.guiaId = guiaId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
