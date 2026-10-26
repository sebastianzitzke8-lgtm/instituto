package edu.instituto.model;

import jakarta.persistence.*;
@Entity @Table(name="inscripcion")
public class Inscripcion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_inscripcion") private Integer id;
    @Column(name="fecha_inscripcion") private java.time.LocalDate fechaInscripcion;
    @Column(name="estado") private String estado = "ACTIVA";
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_alumno",nullable=false) private Alumno alumno;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_curso",nullable=false) private Curso curso;
    public Inscripcion() {}
    public Integer getId(){return id;}
    public java.time.LocalDate getFechaInscripcion(){return fechaInscripcion;}
    public void setFechaInscripcion(java.time.LocalDate value){this.fechaInscripcion=value;}
    public String getEstado(){return estado;}
    public void setEstado(String value){this.estado=value;}
    public Alumno getAlumno(){return alumno;}
    public void setAlumno(Alumno value){this.alumno=value;}
    public Curso getCurso(){return curso;}
    public void setCurso(Curso value){this.curso=value;}
}
