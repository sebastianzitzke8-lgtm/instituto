
package edu.instituto.repository;

import edu.instituto.model.Curso;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CursoRepository
        extends JpaRepository<Curso, Integer> {

    // Evita nombres duplicados al crear un curso.
    boolean existsByNombreAndCicloLectivo_Id(
            String nombre,
            Integer idCicloLectivo
    );

    // Evita nombres duplicados al actualizar un curso,
    // excluyendo el curso que se está modificando.
    boolean existsByNombreAndCicloLectivo_IdAndIdNot(
            String nombre,
            Integer idCicloLectivo,
            Integer idCurso
    );

    // Bloquea el curso mientras se verifican los cupos.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Curso c WHERE c.id = :id")
    Optional<Curso> buscarPorIdConBloqueo(
            @Param("id") Integer id
    );
}