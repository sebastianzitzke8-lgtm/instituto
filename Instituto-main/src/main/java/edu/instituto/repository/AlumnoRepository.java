package edu.instituto.repository;
import java.util.List;
import edu.instituto.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    List<Alumno> findByEstado(String estado);

boolean existsByDni(String dni);

boolean existsByEmail(String email);
}