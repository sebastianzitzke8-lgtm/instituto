package edu.instituto.service;

import edu.instituto.model.Asistencia;
import edu.instituto.model.CicloLectivo;
import edu.instituto.model.Inscripcion;
import edu.instituto.repository.AsistenciaRepository;
import edu.instituto.repository.InscripcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AsistenciaService {
    private static final Set<String> ESTADOS = Set.of("PRESENTE", "AUSENTE", "JUSTIFICADA");
    private final AsistenciaRepository asistencias;
    private final InscripcionRepository inscripciones;

    public AsistenciaService(AsistenciaRepository asistencias, InscripcionRepository inscripciones) {
        this.asistencias = asistencias;
        this.inscripciones = inscripciones;
    }

    @Transactional(readOnly = true)
    public Asistencia buscar(Integer id) {
        validarId(id);
        return asistencias.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asistencia no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Asistencia> porInscripcion(Integer idInscripcion) {
        validarId(idInscripcion);
        if (!inscripciones.existsById(idInscripcion))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada");
        return asistencias.findByInscripcion_IdOrderByFechaDesc(idInscripcion);
    }

    @Transactional(readOnly = true)
    public List<Asistencia> porCursoYFecha(Integer idCurso, LocalDate fecha) {
        validarId(idCurso);
        if (fecha == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha obligatoria");
        return asistencias.findByInscripcion_Curso_IdAndFechaOrderByInscripcion_Alumno_ApellidoAsc(idCurso, fecha);
    }

    @Transactional
    public Asistencia crear(LocalDate fecha, String estado, String observacion, Integer idInscripcion) {
        validarId(idInscripcion);
        Inscripcion inscripcion = obtenerInscripcion(idInscripcion);
        validarDatos(fecha, estado, observacion, inscripcion);
        if (asistencias.existsByInscripcion_IdAndFecha(idInscripcion, fecha))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe asistencia para ese día e inscripción");
        Asistencia asistencia = new Asistencia();
        asignar(asistencia, fecha, estado, observacion, inscripcion);
        return asistencias.save(asistencia);
    }

    @Transactional
    public Asistencia actualizar(Integer id, LocalDate fecha, String estado, String observacion, Integer idInscripcion) {
        Asistencia asistencia = buscar(id);
        validarId(idInscripcion);
        Inscripcion inscripcion = obtenerInscripcion(idInscripcion);
        validarDatos(fecha, estado, observacion, inscripcion);
        if (asistencias.existsByInscripcion_IdAndFechaAndIdNot(idInscripcion, fecha, id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe asistencia para ese día e inscripción");
        asignar(asistencia, fecha, estado, observacion, inscripcion);
        return asistencias.save(asistencia);
    }

    @Transactional
    public void eliminar(Integer id) {
        asistencias.delete(buscar(id));
    }

    private void asignar(Asistencia destino, LocalDate fecha, String estado, String observacion, Inscripcion inscripcion) {
        destino.setFecha(fecha);
        destino.setEstado(estado.trim().toUpperCase(Locale.ROOT));
        destino.setObservacion(observacion == null ? null : observacion.trim());
        destino.setInscripcion(inscripcion);
    }

    private void validarDatos(LocalDate fecha, String estado, String observacion, Inscripcion inscripcion) {
        if (fecha == null || fecha.isAfter(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha es obligatoria y no puede ser futura");
        if (estado == null || !ESTADOS.contains(estado.trim().toUpperCase(Locale.ROOT)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado: PRESENTE, AUSENTE o JUSTIFICADA");
        if (observacion != null && observacion.trim().length() > 255)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Observación demasiado larga");
        if (!"ACTIVA".equalsIgnoreCase(inscripcion.getEstado()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La inscripción no está activa");
        if (inscripcion.getFechaInscripcion() != null && fecha.isBefore(inscripcion.getFechaInscripcion()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha precede a la inscripción");
        CicloLectivo ciclo = inscripcion.getCurso().getCicloLectivo();
        if (ciclo == null || ciclo.getFechaInicio() == null || ciclo.getFechaFin() == null
                || fecha.isBefore(ciclo.getFechaInicio()) || fecha.isAfter(ciclo.getFechaFin()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha fuera del ciclo lectivo");
    }

    private Inscripcion obtenerInscripcion(Integer id) {
        return inscripciones.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
    }

    private void validarId(Integer id) {
        if (id == null || id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido");
    }
}
