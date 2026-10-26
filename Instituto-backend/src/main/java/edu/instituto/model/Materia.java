package edu.instituto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "materia")
public class Materia {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_materia") //clave primaria id_materia
private Integer id;

@NotBlank
@Column(nullable = false, unique = true, length = 50)//no puede ser nulo,longitud maxima 50 y debe ser unico
private String nombre;

@NotBlank
@Column(nullable = false, length = 255)//no puede ser nulo y longitud maxima
private String descripcion;

    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}