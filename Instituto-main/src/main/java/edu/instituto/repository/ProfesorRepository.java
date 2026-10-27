package edu.instituto.repository;
import java.util.List;
import edu.instituto.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
    List<Profesor> findByEstado(String estado);

boolean existsByDni(String dni);

boolean existsByEmail(String email);
}