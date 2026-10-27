package edu.instituto.controller;

import edu.instituto.model.Alumno;
import edu.instituto.service.AlumnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Integer id) {
        alumnoService.darDeBaja(id);
    }
}