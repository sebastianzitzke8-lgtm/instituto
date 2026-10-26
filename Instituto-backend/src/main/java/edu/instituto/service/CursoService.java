package edu.instituto.service;

import edu.instituto.model.*;
import edu.instituto.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class CursoService {
    private final CursoRepository cursos;
    private final CicloLectivoRepository ciclos;
    public CursoService(CursoRepository cursos,CicloLectivoRepository ciclos){this.cursos=cursos;this.ciclos=ciclos;}
    public List<Curso> listar(){return cursos.findAll();}
    public Curso buscar(Integer id){return cursos.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Curso no encontrado"));}
    private void validar(String nombre,Integer capacidad,Integer cicloId,Integer excluirId){
        if(nombre==null||nombre.isBlank()||nombre.length()>50)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nombre obligatorio (máximo 50 caracteres)");
        if(capacidad==null||capacidad<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La capacidad debe ser positiva");
        if(cicloId==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ciclo lectivo obligatorio");
        if(!ciclos.existsById(cicloId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ciclo lectivo no encontrado");
        boolean repetido=excluirId==null?cursos.existsByNombreAndCicloLectivo_Id(nombre,cicloId):cursos.existsByNombreAndCicloLectivo_IdAndIdNot(nombre,cicloId,excluirId);
        if(repetido)throw new ResponseStatusException(HttpStatus.CONFLICT,"Curso duplicado en el mismo ciclo lectivo");
    }
    public Curso crear(String nombre,Integer capacidad,Integer cicloId){
        validar(nombre,capacidad,cicloId,null);
        Curso curso=new Curso();curso.setNombre(nombre);curso.setCapacidad(capacidad);curso.setCicloLectivo(ciclos.findById(cicloId).orElseThrow());
        return cursos.save(curso);
    }
    public Curso actualizar(Integer id,String nombre,Integer capacidad,Integer cicloId){
        Curso curso=buscar(id);validar(nombre,capacidad,cicloId,id);
        curso.setNombre(nombre);curso.setCapacidad(capacidad);curso.setCicloLectivo(ciclos.findById(cicloId).orElseThrow());
        return cursos.save(curso);
    }
    public void eliminar(Integer id){cursos.delete(buscar(id));}
}
