package edu.instituto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class Rol {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_rol") //clave primaria id_rol
private Integer id;

@Column(nullable = false, unique = true, length = 50)//no puede ser nulo y longitud maxima 50
private String nombre;

   // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
    