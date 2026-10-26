package edu.instituto.model;

import jakarta.persistence.*;
@Entity @Table(name="curso_materia")
public class CursoMateria {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_curso_materia") private Integer id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_curso",nullable=false) private Curso curso;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_materia",nullable=false) private Materia materia;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="id_profesor",nullable=false) private Profesor profesor;
    public CursoMateria() {}
    public Integer getId(){return id;}
    public Curso getCurso(){return curso;}
    public void setCurso(Curso value){this.curso=value;}
    public Materia getMateria(){return materia;}
    public void setMateria(Materia value){this.materia=value;}
    public Profesor getProfesor(){return profesor;}
    public void setProfesor(Profesor value){this.profesor=value;}
}
