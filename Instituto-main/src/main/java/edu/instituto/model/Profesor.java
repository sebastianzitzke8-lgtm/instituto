package edu.instituto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "profesor")
@Data //crea los metodos get y set de cada atributo de la clase
public class Profesor {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_profesor") //clave primaria id_profesor
private Integer id;

@NotBlank
@Column(nullable = false, length = 50)//no puede ser nulo y longitud maxima 50
private String nombre;

@NotBlank
@Column(nullable = false, length = 50)//no puede ser nulo y longitud maxima 50
private String apellido;


@NotBlank
@Column(nullable = false, length = 20)//no puede ser nulo y longitud maxima 20
private String telefono;

@NotBlank
@Email
@Column(nullable = false, unique = true, length = 100)//no puede ser nulo, debe ser unico y longitud maxima 100
private String email;

@NotBlank
@Column(nullable = false, unique = true, length = 20)//no puede ser nulo, debe ser unico y longitud maxima 20
private String dni;

@NotBlank 
@Column(nullable = false, length = 100)//no puede ser nulo y longitud maxima 100
private String especialidad;

@Column(nullable = false, length = 20)//longitud maxima 20 y valor por defecto ACTIVO y no puede ser nulo
private String estado = "ACTIVO";

}