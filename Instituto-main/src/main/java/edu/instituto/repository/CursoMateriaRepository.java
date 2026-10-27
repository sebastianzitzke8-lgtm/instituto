package edu.instituto.repository;

import edu.instituto.model.CursoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CursoMateriaRepository extends JpaRepository<CursoMateria,Integer> {

    boolean existsByMateria_Id(Integer idMateria);

    boolean existsByCurso_IdAndMateria_Id(
            Integer idCurso,
            Integer idMateria
    );


}