package edu.instituto.controller;
import edu.instituto.model.Materia;
import edu.instituto.service.MateriaService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")

public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    public List<Materia> listar() {
        return materiaService.listar();
    }

    @GetMapping("/{id}")
    public Materia buscar(@PathVariable Integer id) {
        return materiaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Materia crear(@Valid @RequestBody Materia materia) {
        return materiaService.crear(materia);
    }

    @PutMapping("/{id}")
    public Materia actualizar(@PathVariable Integer id, @Valid @RequestBody Materia materia) {
        return materiaService.actualizar(id, materia);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        materiaService.eliminar(id);
    }
}