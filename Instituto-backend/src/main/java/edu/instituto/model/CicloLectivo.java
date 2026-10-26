package edu.instituto.model;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
@Entity
@Table(name = "ciclo_lectivo")
public class CicloLectivo {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_ciclo_lectivo") //clave primaria id_ciclo_lectivo
private Integer id;

@NotNull
@Column(nullable = false, unique = true) // un solo ciclo por año
private Integer anio;

@NotNull
@Column(name = "fecha_inicio", nullable = false)//no puede ser nulo
private LocalDate fechaInicio;

@NotNull
@Column(name = "fecha_fin", nullable = false)//no puede ser nulo
private LocalDate fechaFin;

@Column(nullable = false, length = 20)//longitud maxima 20
private String estado = "ACTIVO";

    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}