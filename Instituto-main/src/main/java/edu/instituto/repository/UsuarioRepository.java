package edu.instituto.repository;

import edu.instituto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByAlumno_Id(Integer idAlumno);

    Optional<Usuario> findByProfesor_Id(Integer idProfesor);
}
