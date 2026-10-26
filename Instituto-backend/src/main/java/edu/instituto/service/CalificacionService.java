
package edu.instituto.service;

import edu.instituto.model.Alumno;
import edu.instituto.model.Calificacion;
import edu.instituto.model.Evaluacion;

import edu.instituto.repository.AlumnoRepository;
import edu.instituto.repository.CalificacionRepository;
import edu.instituto.repository.EvaluacionRepository;
import edu.instituto.repository.InscripcionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CalificacionService {

    private static final BigDecimal NOTA_MINIMA = BigDecimal.ONE;
    private static final BigDecimal NOTA_MAXIMA = BigDecimal.TEN;

    private static final String INSCRIPCION_ACTIVA = "ACTIVA";

    private final CalificacionRepository repo;
    private final AlumnoRepository alumnoRepo;
    private final EvaluacionRepository evaluacionRepo;
    private final InscripcionRepository inscripcionRepo;

    public CalificacionService(
            CalificacionRepository repo,
            AlumnoRepository alumnoRepo,
            EvaluacionRepository evaluacionRepo,
            InscripcionRepository inscripcionRepo) {

        this.repo = repo;
        this.alumnoRepo = alumnoRepo;
        this.evaluacionRepo = evaluacionRepo;
        this.inscripcionRepo = inscripcionRepo;
    }

    // ==========================================
    // CONSULTAR CALIFICACIONES
    // ==========================================

    @Transactional(readOnly = true)
    public List<Calificacion> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Calificacion buscar(Integer id) {
        validarId(id, "idCalificacion");

        return obtenerCalificacion(id);
    }

    // ==========================================
    // CREAR CALIFICACIÓN
    // ==========================================

    @Transactional
    public Calificacion crear(
            BigDecimal nota,
            Integer idEvaluacion,
            Integer idAlumno) {

        // 1. Validar datos recibidos.
        validarNota(nota);
        validarId(idEvaluacion, "idEvaluacion");
        validarId(idAlumno, "idAlumno");

        // 2. Buscar las entidades relacionadas.
        Evaluacion evaluacion = obtenerEvaluacion(idEvaluacion);
        Alumno alumno = obtenerAlumno(idAlumno);

        // 3. Validar que la evaluación ya haya ocurrido.
        validarFechaEvaluacion(evaluacion);

        // 4. Validar que el alumno esté inscripto
        // en el curso correspondiente.
        validarInscripcionActiva(alumno, evaluacion);

        // 5. Evitar calificaciones duplicadas.
        validarDuplicado(idEvaluacion, idAlumno, null);

        // 6. Crear la calificación.
        Calificacion calificacion = new Calificacion();

        calificacion.setNota(nota);
        calificacion.setEvaluacion(evaluacion);
        calificacion.setAlumno(alumno);

        // 7. Guardar en PostgreSQL.
        return repo.save(calificacion);
    }

    // ==========================================
    // ACTUALIZAR CALIFICACIÓN
    // ==========================================

    @Transactional
    public Calificacion actualizar(
            Integer id,
            BigDecimal nota,
            Integer idEvaluacion,
            Integer idAlumno) {

        validarId(id, "idCalificacion");
        validarNota(nota);
        validarId(idEvaluacion, "idEvaluacion");
        validarId(idAlumno, "idAlumno");

        // Buscar la calificación que vamos a modificar.
        Calificacion calificacion = obtenerCalificacion(id);

        Evaluacion evaluacion = obtenerEvaluacion(idEvaluacion);
        Alumno alumno = obtenerAlumno(idAlumno);

        validarFechaEvaluacion(evaluacion);
        validarInscripcionActiva(alumno, evaluacion);

        // Excluir la calificación actual de la
        // búsqueda de registros duplicados.
        validarDuplicado(idEvaluacion, idAlumno, id);

        calificacion.setNota(nota);
        calificacion.setEvaluacion(evaluacion);
        calificacion.setAlumno(alumno);

        return repo.save(calificacion);
    }

    // ==========================================
    // ELIMINAR CALIFICACIÓN
    // ==========================================

    @Transactional
    public void eliminar(Integer id) {
        validarId(id, "idCalificacion");

        Calificacion calificacion = obtenerCalificacion(id);

        repo.delete(calificacion);
    }

    // ==========================================
    // BÚSQUEDAS
    // ==========================================

    private Calificacion obtenerCalificacion(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Calificación no encontrada"
                ));
    }

    private Evaluacion obtenerEvaluacion(Integer id) {
        return evaluacionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evaluación no encontrada"
                ));
    }

    private Alumno obtenerAlumno(Integer id) {
        return alumnoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alumno no encontrado"
                ));
    }

    // ==========================================
    // VALIDACIONES GENERALES
    // ==========================================

    private void validarId(Integer id, String campo) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    campo + " debe ser un número positivo"
            );
        }
    }


    private void validarNota(BigDecimal nota) {

        // Verificar que la nota no sea nula.
        if (nota == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nota es obligatoria"
            );
        }

        // Definir los límites permitidos.
        BigDecimal notaMinima = BigDecimal.ONE;
        BigDecimal notaMaxima = BigDecimal.TEN;

        // compareTo devuelve:
        // -1 si el primer número es menor
        //  0 si ambos números son iguales
        //  1 si el primer número es mayor

        if (nota.compareTo(notaMinima) < 0 ||
                nota.compareTo(notaMaxima) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nota debe estar entre 1 y 10"
            );
        }
    }


    // ==========================================
    // VALIDACIONES DE NEGOCIO
    // ==========================================

    private void validarFechaEvaluacion(Evaluacion evaluacion) {

        if (evaluacion.getFecha() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La evaluación no tiene una fecha válida"
            );
        }

        if (evaluacion.getFecha().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede calificar una evaluación futura"
            );
        }
    }

    private void validarInscripcionActiva(
            Alumno alumno,
            Evaluacion evaluacion) {

        // Verificar que la evaluación tenga una asignación
        // válida de curso, materia y profesor.
        if (evaluacion.getCursoMateria() == null
                || evaluacion.getCursoMateria().getCurso() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La evaluación no tiene un curso asociado"
            );
        }

        Integer idCurso = evaluacion
                .getCursoMateria()
                .getCurso()
                .getId();

        // Solo pueden recibir notas los alumnos que
        // tengan una inscripción ACTIVA en ese curso.
        boolean estaInscripto = inscripcionRepo
                .existsByAlumno_IdAndCurso_IdAndEstadoIgnoreCase(
                        alumno.getId(),
                        idCurso,
                        INSCRIPCION_ACTIVA
                );

        if (!estaInscripto) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alumno no tiene una inscripción activa "
                            + "en el curso de esta evaluación"
            );
        }
    }

    private void validarDuplicado(
            Integer idEvaluacion,
            Integer idAlumno,
            Integer idCalificacionActual) {

        boolean existeDuplicado;

        if (idCalificacionActual == null) {

            // Caso CREATE.
            existeDuplicado = repo
                    .existsByEvaluacion_IdAndAlumno_Id(
                            idEvaluacion,
                            idAlumno
                    );

        } else {

            // Caso UPDATE: no cuenta el registro actual.
            existeDuplicado = repo
                    .existsByEvaluacion_IdAndAlumno_IdAndIdNot(
                            idEvaluacion,
                            idAlumno,
                            idCalificacionActual
                    );
        }

        if (existeDuplicado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alumno ya tiene una calificación "
                            + "registrada para esta evaluación"
            );
        }
    }
}
