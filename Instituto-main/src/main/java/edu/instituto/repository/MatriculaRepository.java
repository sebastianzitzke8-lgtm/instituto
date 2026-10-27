package edu.instituto.repository;

import edu.instituto.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {
    boolean existsByAlumno_IdAndCicloLectivo_Id(Integer idAlumno, Integer idCicloLectivo);

    List<Matricula> findByAlumno_Id(Integer idAlumno);

    List<Matricula> findByCicloLectivo_Id(Integer idCicloLectivo);
}
