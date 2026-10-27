package edu.instituto.controller;

import edu.instituto.model.Curso;
import edu.instituto.service.CursoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/cursos")
public class CursoController {
    private final CursoService service;
    public CursoController(CursoService service){this.service=service;}
    // DTO de entrada: se reciben IDs, no entidades completas anidadas.
    public record DatosCurso(String nombre,Integer capacidad,Integer idCicloLectivo) {}
    @GetMapping public List<Curso> listar(){return service.listar();}
    @GetMapping("/{id}") public Curso buscar(@PathVariable Integer id){return service.buscar(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Curso crear(@RequestBody DatosCurso datos){return service.crear(datos.nombre(),datos.capacidad(),datos.idCicloLectivo());}
    @PutMapping("/{id}") public Curso actualizar(@PathVariable Integer id,@RequestBody DatosCurso datos){return service.actualizar(id,datos.nombre(),datos.capacidad(),datos.idCicloLectivo());}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){service.eliminar(id);}
}