package edu.instituto.controller;
import edu.instituto.model.Profesor;
import edu.instituto.service.ProfesorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
@RequiredArgsConstructor
public class ProfesorController {

    private final ProfesorService profesorService;

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Integer id) {
        profesorService.darDeBaja(id);
    }
}