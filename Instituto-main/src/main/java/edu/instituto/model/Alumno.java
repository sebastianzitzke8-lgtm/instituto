package edu.instituto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "alumno")
@Data //crea los metodos get y set de cada atributo de la clase
public class Alumno {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_alumno") //clave primaria id_alumno
private Integer id;

@NotBlank
@Column(nullable = false, length = 50)//no puede ser nulo y longitud maxima 50
private String nombre;

@NotBlank
@Column(nullable = false, length = 50)//no puede ser nulo y longitud maxima 50
private String apellido;

@Column(length = 150)//longitud maxima 150
private String domicilio;

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

@NotNull
@Column(name = "fecha_nacimiento", nullable = false)//no puede ser nulo
private LocalDate fechaNacimiento;

@NotNull
@Column(name = "fecha_ingreso", nullable = false)//no puede ser nulo
private LocalDate fechaIngreso;

@Column(nullable = false, length = 20)//longitud maxima 20
private String estado = "ACTIVO";

}