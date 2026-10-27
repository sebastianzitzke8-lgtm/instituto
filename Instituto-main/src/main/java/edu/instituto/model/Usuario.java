package edu.instituto.model;

import jakarta.persistence.*;

// Administrador y personal administrativo son roles de usuario, no entidades propias.
// Un usuario ALUMNO se vincula a un Alumno y un usuario DOCENTE a un Profesor.
@Entity
@Table(name = "usuario")
public class Usuario {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_usuario") //clave primaria id_usuario
private Integer id;

@Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)//no puede ser nulo, debe ser unico y longitud maxima 50
private String nombreUsuario;

@Column(nullable = false, length = 255)//guarda el hash BCrypt, nunca texto plano
private String contrasena;

@ManyToOne(fetch = FetchType.EAGER, optional = false)
@JoinColumn(name = "id_rol", nullable = false)//ADMIN, ADMINISTRATIVO, DOCENTE o ALUMNO
private Rol rol;

@Column(nullable = false, length = 20)//ACTIVO o INACTIVO (baja lógica)
private String estado = "ACTIVO";

@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_alumno", unique = true)//solo para rol ALUMNO
private Alumno alumno;

@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_profesor", unique = true)//solo para rol DOCENTE
private Profesor profesor;

    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
    public Profesor getProfesor() { return profesor; }
    public void setProfesor(Profesor profesor) { this.profesor = profesor; }
}
