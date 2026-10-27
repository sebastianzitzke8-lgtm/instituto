package edu.instituto.controller;

import edu.instituto.model.Evaluacion;
import edu.instituto.service.EvaluacionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/evaluaciones")
public class EvaluacionController {
    private final EvaluacionService service;
    public EvaluacionController(EvaluacionService service){this.service=service;}
    public record Datos(java.time.LocalDate fecha,String tipo,String descripcion,Integer idCursoMateria) {}
    @GetMapping public List<Evaluacion> listar(){return service.listar();}
    @GetMapping("/{id}") public Evaluacion buscar(@PathVariable Integer id){return service.buscar(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Evaluacion crear(@RequestBody Datos datos){return service.crear(datos.fecha(),datos.tipo(),datos.descripcion(),datos.idCursoMateria());}
    @PutMapping("/{id}") public Evaluacion actualizar(@PathVariable Integer id,@RequestBody Datos datos){return service.actualizar(id,datos.fecha(),datos.tipo(),datos.descripcion(),datos.idCursoMateria());}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){service.eliminar(id);}
}