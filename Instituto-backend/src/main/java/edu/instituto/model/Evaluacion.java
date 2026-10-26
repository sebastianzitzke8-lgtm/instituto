package edu.instituto.model;

import jakarta.persistence.*;
@Entity @Table(name="evaluacion")
public class Evaluacion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_evaluacion") private Integer id;
    @Column(name="fecha") private java.time.LocalDate fecha;
    @Column(name="tipo") private String tipo;
    @Column(name="descripcion") private String descripcion;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_curso_materia",nullable=false) private CursoMateria cursoMateria;
    public Evaluacion() {}
    public Integer getId(){return id;}
    public java.time.LocalDate getFecha(){return fecha;}
    public void setFecha(java.time.LocalDate value){this.fecha=value;}
    public String getTipo(){return tipo;}
    public void setTipo(String value){this.tipo=value;}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String value){this.descripcion=value;}
    public CursoMateria getCursoMateria(){return cursoMateria;}
    public void setCursoMateria(CursoMateria value){this.cursoMateria=value;}
}
