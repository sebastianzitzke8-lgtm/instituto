package edu.instituto.service;

import edu.instituto.model.*;
import edu.instituto.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class InscripcionService {

    private final InscripcionRepository repo;
    private final AlumnoRepository alumnoRepo;
    private final CursoRepository cursoRepo;

    public InscripcionService(InscripcionRepository repo,AlumnoRepository alumnoRepo,CursoRepository cursoRepo) {
        this.repo=repo;this.alumnoRepo=alumnoRepo;this.cursoRepo=cursoRepo;
    }

    public List<Inscripcion> listar(){
        return repo.findAll();
    }
    public Inscripcion buscar(Integer id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Inscripcion no encontrado"));}

    @Transactional
    public Inscripcion crear(
            LocalDate fechaInscripcion,
            String estado,
            Integer idAlumno,
            Integer idCurso) {

        // 1. Validar fecha de inscripción
        if (fechaInscripcion == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inscripción es obligatoria"
            );
        }

        if (fechaInscripcion.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inscripción no puede ser futura"
            );
        }

        // 2. Validar estado
        if (estado == null || estado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        String estadoNormalizado = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!estadoNormalizado.equals("ACTIVA")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Una inscripción nueva debe tener estado ACTIVA"
            );
        }

        // 3. Validar los identificadores
        if (idAlumno == null || idAlumno <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "idAlumno debe ser un número positivo"
            );
        }

        if (idCurso == null || idCurso <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "idCurso debe ser un número positivo"
            );
        }

        // 4. Buscar el alumno
        Alumno alumno = alumnoRepo.findById(idAlumno)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alumno no encontrado"
                ));

        // 5. Verificar que el alumno esté activo
        if (!"ACTIVO".equalsIgnoreCase(alumno.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alumno no está activo"
            );
        }

        // 6. Buscar y bloquear el curso
        Curso curso = cursoRepo.buscarPorIdConBloqueo(idCurso)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso no encontrado"
                ));

        // 7. Validar el ciclo lectivo
        if (curso.getCicloLectivo() == null ||
                !"ACTIVO".equalsIgnoreCase(
                        curso.getCicloLectivo().getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El ciclo lectivo no está activo"
            );
        }

        // 8. Evitar inscripciones duplicadas
        if (repo.existsByAlumno_IdAndCurso_Id(idAlumno, idCurso)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alumno ya está inscripto en este curso"
            );
        }

        // 9. Comprobar los cupos disponibles
        long cantidadInscriptos =
                repo.countByCurso_IdAndEstadoIgnoreCase(
                        idCurso,
                        "ACTIVA"
                );

        if (curso.getCapacidad() <= 0 ||
                cantidadInscriptos >= curso.getCapacidad()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El curso no tiene cupos disponibles"
            );
        }

        // 10. Crear la inscripción
        Inscripcion inscripcion = new Inscripcion();

        inscripcion.setFechaInscripcion(fechaInscripcion);
        inscripcion.setEstado(estadoNormalizado);
        inscripcion.setAlumno(alumno);
        inscripcion.setCurso(curso);

        // 11. Guardar en PostgreSQL
        return repo.save(inscripcion);
    }

    public Inscripcion actualizar(Integer id,java.time.LocalDate fechaInscripcion,String estado,Integer idAlumno,Integer idCurso) {
        Inscripcion obj=buscar(id);
        if(fechaInscripcion==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Fecha obligatoria");
        obj.setFechaInscripcion(fechaInscripcion);
        if(estado==null||estado.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Estado obligatorio");
        obj.setEstado(estado);
        if(idAlumno==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idAlumno obligatorio");
        obj.setAlumno(alumnoRepo.findById(idAlumno).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Alumno no encontrado")));
        if(idCurso==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idCurso obligatorio");
        obj.setCurso(cursoRepo.findById(idCurso).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Curso no encontrado")));
        return repo.save(obj);
    }
    public void eliminar(Integer id){repo.delete(buscar(id));}
}