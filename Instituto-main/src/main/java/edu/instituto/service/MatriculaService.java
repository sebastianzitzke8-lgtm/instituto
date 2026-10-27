package edu.instituto.service;

import edu.instituto.model.Alumno;
import edu.instituto.model.CicloLectivo;
import edu.instituto.model.Cuota;
import edu.instituto.model.Matricula;
import edu.instituto.repository.AlumnoRepository;
import edu.instituto.repository.CicloLectivoRepository;
import edu.instituto.repository.CuotaRepository;
import edu.instituto.repository.MatriculaRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

// Reglas de la matrícula (RF21, RF22, RN03, RN04).
@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlumnoRepository alumnoRepository;
    private final CicloLectivoRepository cicloRepository;
    private final CuotaRepository cuotaRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            AlumnoRepository alumnoRepository,
                            CicloLectivoRepository cicloRepository,
                            CuotaRepository cuotaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alumnoRepository = alumnoRepository;
        this.cicloRepository = cicloRepository;
        this.cuotaRepository = cuotaRepository;
    }

    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    public Matricula buscar(Integer id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula no encontrada"));
    }

    public List<Matricula> listarPorAlumno(Integer idAlumno) {
        return matriculaRepository.findByAlumno_Id(idAlumno);
    }

    public List<Matricula> listarPorCiclo(Integer idCicloLectivo) {
        return matriculaRepository.findByCicloLectivo_Id(idCicloLectivo);
    }

    // Registrar matrícula: nace PENDIENTE.
    @Transactional
    public Matricula crear(LocalDate fechaMatricula, BigDecimal importe, Integer idAlumno, Integer idCicloLectivo) {
        if (fechaMatricula == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de matrícula es obligatoria");
        }
        if (fechaMatricula.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de matrícula no puede ser futura");
        }
        if (importe == null || importe.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El importe debe ser mayor a cero");
        }
        if (idAlumno == null || idAlumno <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idAlumno debe ser un número positivo");
        }
        if (idCicloLectivo == null || idCicloLectivo <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idCicloLectivo debe ser un número positivo");
        }

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado"));
        if (!"ACTIVO".equalsIgnoreCase(alumno.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno no está activo");
        }

        CicloLectivo ciclo = cicloRepository.findById(idCicloLectivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciclo lectivo no encontrado"));
        if (!"ACTIVO".equalsIgnoreCase(ciclo.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El ciclo lectivo no está activo");
        }

        // RN04: una sola matrícula por alumno y ciclo lectivo.
        if (matriculaRepository.existsByAlumno_IdAndCicloLectivo_Id(idAlumno, idCicloLectivo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno ya tiene una matrícula en ese ciclo lectivo");
        }

        Matricula matricula = new Matricula();
        matricula.setFechaMatricula(fechaMatricula);
        matricula.setImporte(importe);
        matricula.setEstado("PENDIENTE");
        matricula.setAlumno(alumno);
        matricula.setCicloLectivo(ciclo);
        return matriculaRepository.save(matricula);
    }

    // RF22: PENDIENTE -> ACTIVA o CANCELADA; ACTIVA -> CANCELADA. Una CANCELADA no se modifica.
    // Al cancelar, las cuotas pendientes o vencidas pasan a ANULADA (RN15: una cuota anulada no admite pagos).
    @Transactional
    public Matricula cambiarEstado(Integer id, String estado) {
        if (estado == null || estado.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio");
        }
        String nuevo = estado.trim().toUpperCase(Locale.ROOT);
        if (!nuevo.equals("PENDIENTE") && !nuevo.equals("ACTIVA") && !nuevo.equals("CANCELADA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado inválido. Valores permitidos: PENDIENTE, ACTIVA, CANCELADA");
        }

        Matricula matricula = buscar(id);
        String actual = matricula.getEstado();

        if (actual.equals("CANCELADA")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Una matrícula cancelada no se puede modificar");
        }
        if (actual.equals(nuevo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La matrícula ya está en estado " + nuevo);
        }
        if (nuevo.equals("PENDIENTE")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Una matrícula no puede volver a PENDIENTE");
        }

        if (nuevo.equals("CANCELADA")) {
            List<Cuota> cuotas = cuotaRepository.findByMatricula_IdOrderByNumeroCuotaAsc(id);
            for (Cuota cuota : cuotas) {
                if (cuota.getEstado().equals("PENDIENTE") || cuota.getEstado().equals("VENCIDA")) {
                    cuota.setEstado("ANULADA");
                }
            }
            cuotaRepository.saveAll(cuotas);
        }

        matricula.setEstado(nuevo);
        return matriculaRepository.save(matricula);
    }
}
