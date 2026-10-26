package edu.instituto.controller;
import edu.instituto.model.Profesor;
import edu.instituto.service.ProfesorService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")

public class ProfesorController {

    private final ProfesorService profesorService;

    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @GetMapping
    public List<Profesor> listar() {
        return profesorService.listarActivos();
    }

    @GetMapping("/{id}")
    public Profesor buscar(@PathVariable Integer id) {
        return profesorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profesor crear(@Valid @RequestBody Profesor profesor) {
        return profesorService.crear(profesor);
    }

    // PUT: modifica los datos sin cambiar el estado actual.
    @PutMapping("/{id}")
    public Profesor actualizar(@PathVariable Integer id, @Valid @RequestBody Profesor datos) {
        return profesorService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Integer id) {
        profesorService.darDeBaja(id);
    }
}