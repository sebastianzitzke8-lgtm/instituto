package edu.instituto.service;

import edu.instituto.model.*;
import edu.instituto.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class CursoMateriaService {
    private final CursoMateriaRepository repo;
    private final CursoRepository cursoRepo;
    private final MateriaRepository materiaRepo;
    private final ProfesorRepository profesorRepo;

    public CursoMateriaService(CursoMateriaRepository repo,CursoRepository cursoRepo,MateriaRepository materiaRepo,ProfesorRepository profesorRepo){this.repo=repo;this.cursoRepo=cursoRepo;this.materiaRepo=materiaRepo;this.profesorRepo=profesorRepo;}
    public List<CursoMateria> listar(){
        return repo.findAll();
    }


    public CursoMateria buscar(Integer id){
        return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"CursoMateria no encontrado"));
    }


    public CursoMateria crear(Integer idCurso,Integer idMateria,Integer idProfesor) {
        CursoMateria obj=new CursoMateria();

        if(idCurso==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idCurso obligatorio");

        obj.setCurso(cursoRepo.findById(idCurso).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Curso no encontrado")));

        if(idMateria==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idMateria obligatorio");

        obj.setMateria(materiaRepo.findById(idMateria).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Materia no encontrado")));
        if(idProfesor==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idProfesor obligatorio");
        obj.setProfesor(profesorRepo.findById(idProfesor).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profesor no encontrado")));

        if (repo.existsByCurso_IdAndMateria_Id(
                idCurso, idMateria)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La materia ya está asignada a este curso"
            );
        }

        return repo.save(obj);
    }

    public CursoMateria actualizar(Integer id,Integer idCurso,Integer idMateria,Integer idProfesor) {
        CursoMateria obj=buscar(id);

        if(idCurso==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idCurso obligatorio");

        obj.setCurso(cursoRepo.findById(idCurso).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Curso no encontrado")));

        if(idMateria==null)

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idMateria obligatorio");

        obj.setMateria(materiaRepo.findById(idMateria).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Materia no encontrado")));

        if(idProfesor==null)

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"idProfesor obligatorio");

        obj.setProfesor(profesorRepo.findById(idProfesor).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profesor no encontrado")));

        return repo.save(obj);
    }

    public void eliminar(Integer id){
        repo.delete(buscar(id));
    }
}