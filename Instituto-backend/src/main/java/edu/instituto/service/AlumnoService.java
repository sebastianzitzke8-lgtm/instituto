package edu.instituto.service;
import edu.instituto.model.Alumno;
import edu.instituto.repository.AlumnoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

//La capa de servicio se encarga de aplicar las reglas del sistema antes de guardar o buscar alumnos en la base de datos.
@Service //indica que esta clase contiene las reglas que debe cumplir el sistema

public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    //listar solo alumnos activos
    public List<Alumno> listarActivos() {
        return alumnoRepository.findByEstado("ACTIVO");
    }
    // buscar por id, si no lo encuentra lanza una excepcion
    public Alumno buscarPorId(Integer id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alumno no encontrado"));
    }

    //Registrar alumno(el DNI y el email no se pueden repetir)
    public Alumno crear(Alumno alumno) {
        if (alumnoRepository.existsByDni(alumno.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un alumno con ese DNI");
        }
        if (alumnoRepository.existsByEmail(alumno.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un alumno con ese email");
        }
        alumno.setId(null);
        alumno.setEstado("ACTIVO");
        return alumnoRepository.save(alumno);
    }

    // Actualiza un registro sin modificar su identificador ni su estado.
    public Alumno actualizar(Integer id, Alumno datos) {
        Alumno actual = buscarPorId(id);
        if (alumnoRepository.existsByDni(datos.getDni()) && !datos.getDni().equals(actual.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro alumno con ese DNI");
        }
        if (alumnoRepository.existsByEmail(datos.getEmail()) && !datos.getEmail().equals(actual.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro alumno con ese email");
        }
        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setTelefono(datos.getTelefono());
        actual.setEmail(datos.getEmail());
        actual.setDni(datos.getDni());
        actual.setDomicilio(datos.getDomicilio());
        actual.setFechaNacimiento(datos.getFechaNacimiento());
        actual.setFechaIngreso(datos.getFechaIngreso());
        return alumnoRepository.save(actual);
    }

    //Baja lógica, no se borra el registro
    public void darDeBaja(Integer id) {
        Alumno alumno = buscarPorId(id);
        alumno.setEstado("INACTIVO");
        alumnoRepository.save(alumno);
    }
}