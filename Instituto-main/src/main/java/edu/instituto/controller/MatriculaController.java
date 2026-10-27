package edu.instituto.controller;

import edu.instituto.model.Matricula;
import edu.instituto.service.MatriculaService;
import edu.instituto.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    public record Datos(LocalDate fechaMatricula, BigDecimal importe, Integer idAlumno, Integer idCicloLectivo) {}
    public record EstadoDatos(String estado) {}

    private final MatriculaService service;
    private final UsuarioService usuarioService;

    public MatriculaController(MatriculaService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Matricula> listar() {
        return service.listar();
    }

    // Vista del alumno logueado (RF29, RN17): el id sale de la sesión, no de la URL.
    @GetMapping("/mis-matriculas")
    public List<Matricula> misMatriculas(Authentication authentication) {
        return service.listarPorAlumno(usuarioService.idAlumnoDe(authentication.getName()));
    }

    @GetMapping("/{id}")
    public Matricula buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @GetMapping("/alumno/{idAlumno}")
    public List<Matricula> porAlumno(@PathVariable Integer idAlumno) {
        return service.listarPorAlumno(idAlumno);
    }

    @GetMapping("/ciclo/{idCicloLectivo}")
    public List<Matricula> porCiclo(@PathVariable Integer idCicloLectivo) {
        return service.listarPorCiclo(idCicloLectivo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Matricula crear(@RequestBody Datos datos) {
        return service.crear(datos.fechaMatricula(), datos.importe(), datos.idAlumno(), datos.idCicloLectivo());
    }

    @PatchMapping("/{id}/estado")
    public Matricula cambiarEstado(@PathVariable Integer id, @RequestBody EstadoDatos datos) {
        return service.cambiarEstado(id, datos.estado());
    }
}