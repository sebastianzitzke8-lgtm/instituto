package edu.instituto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// RN03: pertenece a un único alumno y a un único ciclo lectivo.
// RN04: un alumno no puede tener más de una matrícula por ciclo lectivo (restricción única).
@Entity
@Table(name = "matricula",
       uniqueConstraints = @UniqueConstraint(name = "uk_matricula_alumno_ciclo",
                                             columnNames = {"id_alumno", "id_ciclo_lectivo"}))
public class Matricula {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_matricula") //clave primaria id_matricula
private Integer id;

@Column(name = "fecha_matricula", nullable = false)//no puede ser nulo
private LocalDate fechaMatricula;

@Column(nullable = false, precision = 12, scale = 2)//importe de la matrícula
private BigDecimal importe;

@Column(nullable = false, length = 20)//PENDIENTE, ACTIVA o CANCELADA
private String estado = "PENDIENTE";

@ManyToOne(fetch = FetchType.EAGER, optional = false)
@JoinColumn(name = "id_alumno", nullable = false)
private Alumno alumno;

@ManyToOne(fetch = FetchType.EAGER, optional = false)
@JoinColumn(name = "id_ciclo_lectivo", nullable = false)
private CicloLectivo cicloLectivo;

    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(LocalDate fechaMatricula) { this.fechaMatricula = fechaMatricula; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
    public CicloLectivo getCicloLectivo() { return cicloLectivo; }
    public void setCicloLectivo(CicloLectivo cicloLectivo) { this.cicloLectivo = cicloLectivo; }
}
