package edu.instituto.repository;
import java.util.List;
import edu.instituto.model.CicloLectivo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CicloLectivoRepository extends JpaRepository<CicloLectivo, Integer> {

    List<CicloLectivo> findByEstado(String estado);

    boolean existsByAnio(Integer anio);
    boolean existsByAnioAndIdNot(Integer anio, Integer id);
}
