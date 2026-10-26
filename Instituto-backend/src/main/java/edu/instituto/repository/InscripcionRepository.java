
package edu.instituto.repository;

import edu.instituto.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository
        extends JpaRepository<Inscripcion, Integer> {

    // Comprueba si el alumno ya tiene una inscripción en el curso.
    boolean existsByAlumno_IdAndCurso_Id(
            Integer idAlumno,
            Integer idCurso
    );

    // Evita duplicados al actualizar, excluyendo la inscripción actual.
    boolean existsByAlumno_IdAndCurso_IdAndIdNot(
            Integer idAlumno,
            Integer idCurso,
            Integer idInscripcion
    );

    // Cuenta solamente las inscripciones que ocupan cupo.
    long countByCurso_IdAndEstadoIgnoreCase(
            Integer idCurso,
            String estado
    );


    boolean existsByAlumno_IdAndCurso_IdAndEstadoIgnoreCase(
            Integer idAlumno,
            Integer idCurso,
            String estado
    );

}
