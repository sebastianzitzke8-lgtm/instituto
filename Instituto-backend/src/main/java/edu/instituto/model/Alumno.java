package edu.instituto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "alumno")
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


    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}