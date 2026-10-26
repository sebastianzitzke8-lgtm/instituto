package edu.instituto.controller;

import edu.instituto.model.Alumno;
import edu.instituto.service.AlumnoService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")

public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public List<Alumno> listar() {
        return alumnoService.listarActivos();
    }

    @GetMapping("/{id}")
    public Alumno buscar(@PathVariable Integer id) {
        return alumnoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno crear(@Valid @RequestBody Alumno alumno) {
        return alumnoService.crear(alumno);
    }

    // PUT: modifica los datos sin cambiar el estado actual.
    @PutMapping("/{id}")
    public Alumno actualizar(@PathVariable Integer id, @Valid @RequestBody Alumno datos) {
        return alumnoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Integer id) {
        alumnoService.darDeBaja(id);
    }
}