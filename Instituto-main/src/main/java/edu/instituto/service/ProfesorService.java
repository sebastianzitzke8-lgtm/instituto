package edu.instituto.service;
import edu.instituto.model.Profesor;
import edu.instituto.repository.ProfesorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

//La capa de servicio se encarga de aplicar las reglas del sistema antes de guardar o buscar profesores en la base de datos.
@Service //indica que esta clase contiene las reglas que debe cumplir el sistema
@RequiredArgsConstructor
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    //listar solo profesores activos
    public List<Profesor> listarActivos() {
        return profesorRepository.findByEstado("ACTIVO");
    }
    // buscar por id, si no lo encuentra lanza una excepcion
    public Profesor buscarPorId(Integer id) {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profesor no encontrado"));
    }

    //Registrar profesor(el DNI y el email no se pueden repetir)
    public Profesor crear(Profesor profesor) {
        if (profesorRepository.existsByDni(profesor.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un profesor con ese DNI");
        }
        if (profesorRepository.existsByEmail(profesor.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un profesor con ese email");
        }
        profesor.setId(null);
        profesor.setEstado("ACTIVO");
        return profesorRepository.save(profesor);
    }

    //Baja lógica, no se borra el registro
    public void darDeBaja(Integer id) {
        Profesor profesor = buscarPorId(id);
        profesor.setEstado("INACTIVO");
        profesorRepository.save(profesor);
    }
}