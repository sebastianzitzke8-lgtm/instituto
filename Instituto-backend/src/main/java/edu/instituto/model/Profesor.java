package edu.instituto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "profesor")
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


    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}