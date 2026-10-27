package edu.instituto.repository;

import edu.instituto.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {
    boolean existsByInscripcion_IdAndFecha(Integer idInscripcion, LocalDate fecha);
    boolean existsByInscripcion_IdAndFechaAndIdNot(Integer idInscripcion, LocalDate fecha, Integer id);
    List<Asistencia> findByInscripcion_IdOrderByFechaDesc(Integer idInscripcion);
    List<Asistencia> findByInscripcion_Curso_IdAndFechaOrderByInscripcion_Alumno_ApellidoAsc(Integer idCurso, LocalDate fecha);
}
