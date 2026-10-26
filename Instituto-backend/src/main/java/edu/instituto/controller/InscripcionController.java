package edu.instituto.controller;

import edu.instituto.model.Inscripcion;
import edu.instituto.service.InscripcionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/inscripciones")
public class InscripcionController {
    private final InscripcionService service;
    public InscripcionController(InscripcionService service){this.service=service;}
    public record Datos(java.time.LocalDate fechaInscripcion,String estado,Integer idAlumno,Integer idCurso) {}
    @GetMapping public List<Inscripcion> listar(){return service.listar();}
    @GetMapping("/{id}") public Inscripcion buscar(@PathVariable Integer id){return service.buscar(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Inscripcion crear(@RequestBody Datos datos){return service.crear(datos.fechaInscripcion(),datos.estado(),datos.idAlumno(),datos.idCurso());}
    @PutMapping("/{id}") public Inscripcion actualizar(@PathVariable Integer id,@RequestBody Datos datos){return service.actualizar(id,datos.fechaInscripcion(),datos.estado(),datos.idAlumno(),datos.idCurso());}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){service.eliminar(id);}
}
