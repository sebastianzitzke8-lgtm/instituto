package edu.instituto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// RN05: toda cuota está asociada a una matrícula. No puede repetirse el número de cuota dentro de una matrícula.
@Entity
@Table(name = "cuota",
       uniqueConstraints = @UniqueConstraint(name = "uk_cuota_matricula_numero",
                                             columnNames = {"id_matricula", "numero_cuota"}))
public class Cuota {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_cuota") //clave primaria id_cuota
private Integer id;

@Column(name = "numero_cuota", nullable = false)//1, 2, 3...
private Integer numeroCuota;

@Column(nullable = false, precision = 12, scale = 2)
private BigDecimal importe;

@Column(name = "fecha_vencimiento", nullable = false)
private LocalDate fechaVencimiento;

@Column(nullable = false, length = 20)//PENDIENTE, PAGADA, VENCIDA o ANULADA
private String estado = "PENDIENTE";

@ManyToOne(fetch = FetchType.EAGER, optional = false)
@JoinColumn(name = "id_matricula", nullable = false)
private Matricula matricula;

    // Métodos explícitos: no se utiliza Lombok.
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(Integer numeroCuota) { this.numeroCuota = numeroCuota; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Matricula getMatricula() { return matricula; }
    public void setMatricula(Matricula matricula) { this.matricula = matricula; }
}
