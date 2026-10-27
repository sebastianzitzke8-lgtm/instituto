package edu.instituto.service;

import edu.instituto.model.Cuota;
import edu.instituto.model.Matricula;
import edu.instituto.repository.AlumnoRepository;
import edu.instituto.repository.CuotaRepository;
import edu.instituto.repository.MatriculaRepository;
import edu.instituto.repository.PagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Reglas de las cuotas: generación, consulta, anulación y deuda (RF23, RF24, RF27, RN05, RN08, RN15).
@Service
public class CuotaService {

    private static final int MAX_CUOTAS = 12; // provisional: confirmar con la consigna

    // Resumen de deuda de un alumno (RF27). El saldo ya descuenta pagos parciales; se puede ampliar con total abonado.
    public record Deuda(Integer idAlumno,
                        long cuotasPendientes,
                        long cuotasVencidas,
                        BigDecimal importePendiente,
                        BigDecimal importeVencido,
                        BigDecimal importeAdeudado,
                        List<Cuota> cuotas) {}

    private final CuotaRepository cuotaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AlumnoRepository alumnoRepository;
    private final PagoRepository pagoRepository;

    public CuotaService(CuotaRepository cuotaRepository,
                        MatriculaRepository matriculaRepository,
                        AlumnoRepository alumnoRepository, PagoRepository pagoRepository) {
        this.cuotaRepository = cuotaRepository;
        this.matriculaRepository = matriculaRepository;
        this.alumnoRepository = alumnoRepository;
        this.pagoRepository = pagoRepository;
    }

    // RN08: antes de consultar, las cuotas pendientes con fecha pasada se marcan VENCIDA.
    private void actualizarVencidas() {
        cuotaRepository.marcarVencidas(LocalDate.now());
    }

    @Transactional
    public Cuota buscar(Integer id) {
        actualizarVencidas();
        return cuotaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota no encontrada"));
    }

    @Transactional
    public List<Cuota> listarPorMatricula(Integer idMatricula) {
        if (!matriculaRepository.existsById(idMatricula)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula no encontrada");
        }
        actualizarVencidas();
        return cuotaRepository.findByMatricula_IdOrderByNumeroCuotaAsc(idMatricula);
    }

    @Transactional
    public List<Cuota> listarPorAlumno(Integer idAlumno) {
        validarAlumno(idAlumno);
        actualizarVencidas();
        return cuotaRepository.findByMatricula_Alumno_IdOrderByFechaVencimientoAsc(idAlumno);
    }

    // RF23: genera las cuotas mensuales de una matrícula (la primera vence en primerVencimiento, las demás cada mes).
    @Transactional
    public List<Cuota> generar(Integer idMatricula, Integer cantidad, BigDecimal importe, LocalDate primerVencimiento) {
        if (idMatricula == null || idMatricula <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idMatricula debe ser un número positivo");
        }
        if (cantidad == null || cantidad < 1 || cantidad > MAX_CUOTAS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad de cuotas debe estar entre 1 y " + MAX_CUOTAS);
        }
        if (importe == null || importe.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El importe de la cuota debe ser mayor a cero");
        }
        if (primerVencimiento == null || primerVencimiento.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El primer vencimiento es obligatorio y no puede ser anterior a hoy");
        }

        Matricula matricula = matriculaRepository.findById(idMatricula)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula no encontrada"));
        if ("CANCELADA".equals(matricula.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pueden generar cuotas para una matrícula cancelada");
        }
        if (cuotaRepository.existsByMatricula_Id(idMatricula)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La matrícula ya tiene cuotas generadas");
        }

        List<Cuota> cuotas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Cuota cuota = new Cuota();
            cuota.setNumeroCuota(i + 1);
            cuota.setImporte(importe);
            cuota.setFechaVencimiento(primerVencimiento.plusMonths(i));
            cuota.setEstado("PENDIENTE");
            cuota.setMatricula(matricula); // RN05
            cuotas.add(cuota);
        }
        return cuotaRepository.saveAll(cuotas);
    }

    // Anular una cuota. Una cuota PAGADA no se anula (tiene pagos registrados, RN06).
    @Transactional
    public Cuota anular(Integer id) {
        Cuota cuota = buscar(id);
        if ("PAGADA".equals(cuota.getEstado()) || pagoRepository.existsByCuota_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede anular una cuota pagada");
        }
        if ("ANULADA".equals(cuota.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cuota ya está anulada");
        }
        cuota.setEstado("ANULADA");
        return cuotaRepository.save(cuota);
    }

    @Transactional
    public Deuda deuda(Integer idAlumno) {
        validarAlumno(idAlumno);
        actualizarVencidas();
        List<Cuota> cuotas = cuotaRepository.findByMatricula_Alumno_IdAndEstadoInOrderByFechaVencimientoAsc(
                idAlumno, List.of("PENDIENTE", "VENCIDA"));

        long pendientes = 0;
        long vencidas = 0;
        BigDecimal importePendiente = BigDecimal.ZERO;
        BigDecimal importeVencido = BigDecimal.ZERO;
        for (Cuota cuota : cuotas) {
            // Sólo se cuenta el saldo: los pagos parciales reducen la deuda.
            BigDecimal saldo = cuota.getImporte().subtract(pagoRepository.totalPagadoPorCuota(cuota.getId()));
            if (saldo.signum() < 0) saldo = BigDecimal.ZERO;
            if ("VENCIDA".equals(cuota.getEstado())) {
                vencidas++;
                importeVencido = importeVencido.add(saldo);
            } else {
                pendientes++;
                importePendiente = importePendiente.add(saldo);
            }
        }
        return new Deuda(idAlumno, pendientes, vencidas, importePendiente, importeVencido,
                importePendiente.add(importeVencido), cuotas);
    }

    private void validarAlumno(Integer idAlumno) {
        if (!alumnoRepository.existsById(idAlumno)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado");
        }
    }
}
