
package edu.instituto.repository;

import edu.instituto.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalificacionRepository
        extends JpaRepository<Calificacion, Integer> {

    // Comprueba si un alumno ya tiene una calificación
    // registrada para una evaluación.
    boolean existsByEvaluacion_IdAndAlumno_Id(
            Integer idEvaluacion,
            Integer idAlumno
    );

    // Comprueba duplicados al actualizar una nota,
    // sin contar la calificación que estamos editando.
    boolean existsByEvaluacion_IdAndAlumno_IdAndIdNot(
            Integer idEvaluacion,
            Integer idAlumno,
            Integer idCalificacion
    );
}
