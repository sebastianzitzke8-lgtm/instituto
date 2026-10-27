package edu.instituto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "materia")
@Data //crea los metodos get y set de cada atributo de la clase
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
}