package edu.instituto.model;

import jakarta.persistence.*;
@Entity @Table(name="curso")
public class Curso {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_curso")
    private Integer id;
    @Column(nullable=false,length=50) private String nombre;
    @Column(nullable=false) private Integer capacidad;
    // Muchos cursos pueden pertenecer al mismo ciclo lectivo.
    @ManyToOne(fetch=FetchType.EAGER,optional=false)
    @JoinColumn(name="id_ciclo_lectivo",nullable=false)
    private CicloLectivo cicloLectivo;
    public Curso() {}

    public Integer getId(){
        return id;}
    public String getNombre(){
        return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public Integer getCapacidad(){
        return capacidad;}
    public void setCapacidad(Integer capacidad){this.capacidad=capacidad;}
    public CicloLectivo getCicloLectivo(){
        return cicloLectivo;}
    public void setCicloLectivo(CicloLectivo cicloLectivo){this.cicloLectivo=cicloLectivo;}
}