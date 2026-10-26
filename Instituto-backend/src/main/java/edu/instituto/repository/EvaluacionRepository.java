package edu.instituto.repository;



import edu.instituto.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EvaluacionRepository
        extends JpaRepository<Evaluacion, Integer> {

    // Verifica si existe una evaluación con la misma fecha,
    // tipo y asignación de curso-materia.
    boolean existsByFechaAndTipoIgnoreCaseAndCursoMateria_Id(
            LocalDate fecha,
            String tipo,
            Integer idCursoMateria
    );

    // Hace la misma comprobación, pero excluye la evaluación
    // que estamos modificando.
    boolean existsByFechaAndTipoIgnoreCaseAndCursoMateria_IdAndIdNot(
            LocalDate fecha,
            String tipo,
            Integer idCursoMateria,
            Integer idEvaluacion
    );
}
