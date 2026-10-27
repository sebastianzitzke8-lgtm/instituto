package edu.instituto.controller;

import edu.instituto.model.CursoMateria;
import edu.instituto.service.CursoMateriaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/curso-materias")
public class CursoMateriaController {
    private final CursoMateriaService service;
    public CursoMateriaController(CursoMateriaService service){this.service=service;}
    public record Datos(Integer idCurso,Integer idMateria,Integer idProfesor) {}
    @GetMapping public List<CursoMateria> listar(){return service.listar();}
    @GetMapping("/{id}") public CursoMateria buscar(@PathVariable Integer id){return service.buscar(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CursoMateria crear(@RequestBody Datos datos){return service.crear(datos.idCurso(),datos.idMateria(),datos.idProfesor());}
    @PutMapping("/{id}") public CursoMateria actualizar(@PathVariable Integer id,@RequestBody Datos datos){return service.actualizar(id,datos.idCurso(),datos.idMateria(),datos.idProfesor());}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){service.eliminar(id);}
}