package edu.instituto.model;

import jakarta.persistence.*;
@Entity @Table(name="calificacion")
public class Calificacion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_calificacion") private Integer id;
    @Column(name="nota") private java.math.BigDecimal nota;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_evaluacion",nullable=false) private Evaluacion evaluacion;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_alumno",nullable=false) private Alumno alumno;
    public Calificacion() {}
    public Integer getId(){return id;}
    public java.math.BigDecimal getNota(){return nota;}
    public void setNota(java.math.BigDecimal value){this.nota=value;}
    public Evaluacion getEvaluacion(){return evaluacion;}
    public void setEvaluacion(Evaluacion value){this.evaluacion=value;}
    public Alumno getAlumno(){return alumno;}
    public void setAlumno(Alumno value){this.alumno=value;}
}
