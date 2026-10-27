
package edu.instituto.service;

import edu.instituto.model.CursoMateria;
import edu.instituto.model.Evaluacion;

import edu.instituto.repository.CursoMateriaRepository;
import edu.instituto.repository.EvaluacionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class EvaluacionService {

    // Tipos de evaluación permitidos.
    private static final Set<String> TIPOS_VALIDOS = Set.of(
            "PARCIAL",
            "FINAL",
            "TRABAJO_PRACTICO"
    );

    private static final String ESTADO_ACTIVO = "ACTIVO";

    private final EvaluacionRepository evaluacionRepository;
    private final CursoMateriaRepository cursoMateriaRepository;

    // Inyección de dependencias por constructor.
    public EvaluacionService(
            EvaluacionRepository evaluacionRepository,
            CursoMateriaRepository cursoMateriaRepository) {

        this.evaluacionRepository = evaluacionRepository;
        this.cursoMateriaRepository = cursoMateriaRepository;
    }

    // =========================================
    // CONSULTAR EVALUACIONES
    // =========================================

    @Transactional(readOnly = true)
    public List<Evaluacion> listar() {
        return evaluacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Evaluacion buscar(Integer id) {

        validarId(id, "idEvaluacion");

        return obtenerEvaluacion(id);
    }

    // =========================================
    // CREAR EVALUACIÓN
    // =========================================

    @Transactional
    public Evaluacion crear(
            LocalDate fecha,
            String tipo,
            String descripcion,
            Integer idCursoMateria) {

        // 1. Validar los datos recibidos.
        validarFecha(fecha);
        validarDescripcion(descripcion);
        validarId(idCursoMateria, "idCursoMateria");

        String tipoNormalizado = normalizarTipo(tipo);

        // 2. Buscar la asignación entre curso, materia y profesor.
        CursoMateria cursoMateria =
                obtenerCursoMateria(idCursoMateria);

        // 3. Comprobar que el ciclo lectivo esté activo.
        validarCicloLectivoActivo(cursoMateria);

        // 4. Evitar evaluaciones duplicadas.
        validarDuplicado(
                fecha,
                tipoNormalizado,
                idCursoMateria,
                null
        );

        // 5. Crear la evaluación.
        Evaluacion evaluacion = new Evaluacion();

        evaluacion.setFecha(fecha);
        evaluacion.setTipo(tipoNormalizado);
        evaluacion.setDescripcion(descripcion.trim());
        evaluacion.setCursoMateria(cursoMateria);

        // 6. Guardar en PostgreSQL.
        return evaluacionRepository.save(evaluacion);
    }

    // =========================================
    // ACTUALIZAR EVALUACIÓN
    // =========================================

    @Transactional
    public Evaluacion actualizar(
            Integer id,
            LocalDate fecha,
            String tipo,
            String descripcion,
            Integer idCursoMateria) {

        // 1. Validar los identificadores y datos.
        validarId(id, "idEvaluacion");
        validarFecha(fecha);
        validarDescripcion(descripcion);
        validarId(idCursoMateria, "idCursoMateria");

        String tipoNormalizado = normalizarTipo(tipo);

        // 2. Buscar la evaluación existente.
        Evaluacion evaluacion = obtenerEvaluacion(id);

        // 3. Buscar la asignación seleccionada.
        CursoMateria cursoMateria =
                obtenerCursoMateria(idCursoMateria);

        // 4. Validar ciclo lectivo.
        validarCicloLectivoActivo(cursoMateria);

        // 5. Comprobar duplicados excluyendo el registro actual.
        validarDuplicado(
                fecha,
                tipoNormalizado,
                idCursoMateria,
                id
        );

        // 6. Actualizar los campos.
        evaluacion.setFecha(fecha);
        evaluacion.setTipo(tipoNormalizado);
        evaluacion.setDescripcion(descripcion.trim());
        evaluacion.setCursoMateria(cursoMateria);

        // 7. Guardar los cambios.
        return evaluacionRepository.save(evaluacion);
    }

    // =========================================
    // ELIMINAR EVALUACIÓN
    // =========================================

    @Transactional
    public void eliminar(Integer id) {

        validarId(id, "idEvaluacion");

        Evaluacion evaluacion = obtenerEvaluacion(id);

        // Si existen calificaciones relacionadas, la BD puede
        // impedir la eliminación mediante una clave foránea.
        // Más adelante agregaremos una validación explícita.
        evaluacionRepository.delete(evaluacion);
    }

    // =========================================
    // MÉTODOS DE BÚSQUEDA
    // =========================================

    private Evaluacion obtenerEvaluacion(Integer id) {

        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evaluación no encontrada"
                ));
    }

    private CursoMateria obtenerCursoMateria(Integer id) {

        return cursoMateriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La asignación CursoMateria no existe"
                ));
    }

    // =========================================
    // VALIDACIONES GENERALES
    // =========================================

    private void validarId(Integer id, String nombreCampo) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    nombreCampo + " debe ser un número positivo"
            );
        }
    }

    private void validarFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de evaluación es obligatoria"
            );
        }

        // Se permiten evaluaciones futuras porque un instituto
        // necesita programar exámenes con anticipación.
        // No restringimos la fecha a LocalDate.now().
    }

    private void validarDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción es obligatoria"
            );
        }

        if (descripcion.trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción no puede superar 255 caracteres"
            );
        }
    }

    private String normalizarTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de evaluación es obligatorio"
            );
        }

        String tipoNormalizado = tipo.trim()
                .toUpperCase(Locale.ROOT);

        if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipos permitidos: PARCIAL, FINAL, TRABAJO_PRACTICO"
            );
        }

        return tipoNormalizado;
    }

    // =========================================
    // VALIDACIONES DE NEGOCIO
    // =========================================

    private void validarCicloLectivoActivo(
            CursoMateria cursoMateria) {

        // Obtenemos el ciclo lectivo a través del curso
        // relacionado con la asignación CursoMateria.
        if (cursoMateria.getCurso() == null
                || cursoMateria.getCurso().getCicloLectivo() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La asignación no tiene un ciclo lectivo válido"
            );
        }

        String estadoCiclo = cursoMateria
                .getCurso()
                .getCicloLectivo()
                .getEstado();

        if (!ESTADO_ACTIVO.equalsIgnoreCase(estadoCiclo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pueden registrar evaluaciones "
                            + "en un ciclo lectivo inactivo"
            );
        }
    }

    private void validarDuplicado(
            LocalDate fecha,
            String tipo,
            Integer idCursoMateria,
            Integer idEvaluacionActual) {

        boolean existeDuplicado;

        if (idEvaluacionActual == null) {

            // Caso CREATE: buscamos cualquier coincidencia.
            existeDuplicado = evaluacionRepository
                    .existsByFechaAndTipoIgnoreCaseAndCursoMateria_Id(
                            fecha,
                            tipo,
                            idCursoMateria
                    );

        } else {

            // Caso UPDATE: excluimos la evaluación editada.
            existeDuplicado = evaluacionRepository
                    .existsByFechaAndTipoIgnoreCaseAndCursoMateria_IdAndIdNot(
                            fecha,
                            tipo,
                            idCursoMateria,
                            idEvaluacionActual
                    );
        }

        if (existeDuplicado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una evaluación del mismo tipo "
                            + "para esta materia y fecha"
            );
        }
    }
}