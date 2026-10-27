package edu.instituto.model;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "ciclo_lectivo")
@Data //crea los metodos get y set de cada atributo de la clase
public class CicloLectivo {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_ciclo_lectivo") //clave primaria id_ciclo_lectivo
private Integer id;

@NotNull
@Column(nullable = false, unique = true) // un solo ciclo por año
private Integer anio;

@NotNull
@Column(name = "fecha_inicio", nullable = false)//no puede ser nulo
private LocalDate fechaInicio;

@NotNull
@Column(name = "fecha_fin", nullable = false)//no puede ser nulo
private LocalDate fechaFin;

@Column(nullable = false, length = 20)//longitud maxima 20
private String estado = "ACTIVO";
}