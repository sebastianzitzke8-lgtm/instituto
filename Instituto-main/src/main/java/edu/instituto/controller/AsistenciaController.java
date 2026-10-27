package edu.instituto.controller;

import edu.instituto.model.Asistencia;
import edu.instituto.service.AsistenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {
    public record Datos(LocalDate fecha, String estado, String observacion, Integer idInscripcion) {}
    private final AsistenciaService service;
    public AsistenciaController(AsistenciaService service) { this.service = service; }

    @GetMapping("/{id}")
    public Asistencia buscar(@PathVariable Integer id) { return service.buscar(id); }

    @GetMapping("/inscripcion/{idInscripcion}")
    public List<Asistencia> porInscripcion(@PathVariable Integer idInscripcion) { return service.porInscripcion(idInscripcion); }

    @GetMapping("/curso/{idCurso}")
    public List<Asistencia> porCurso(@PathVariable Integer idCurso, @RequestParam LocalDate fecha) {
        return service.porCursoYFecha(idCurso, fecha);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Asistencia crear(@RequestBody Datos datos) {
        return service.crear(datos.fecha(), datos.estado(), datos.observacion(), datos.idInscripcion());
    }

    @PutMapping("/{id}")
    public Asistencia actualizar(@PathVariable Integer id, @RequestBody Datos datos) {
        return service.actualizar(id, datos.fecha(), datos.estado(), datos.observacion(), datos.idInscripcion());
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { service.eliminar(id); }
}
