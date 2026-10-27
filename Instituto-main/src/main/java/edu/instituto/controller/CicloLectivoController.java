package edu.instituto.controller;
import edu.instituto.model.CicloLectivo;
import edu.instituto.service.CicloLectivoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciclos-lectivos")
@RequiredArgsConstructor
public class CicloLectivoController {

    private final CicloLectivoService cicloService;

    @GetMapping
    public List<CicloLectivo> listar() {
        return cicloService.listar();
    }

    @GetMapping("/{id}")
    public CicloLectivo buscar(@PathVariable Integer id) {
        return cicloService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CicloLectivo crear(@Valid @RequestBody CicloLectivo ciclo) {
        return cicloService.crear(ciclo);
    }

    @PutMapping("/{id}")
    public CicloLectivo actualizar(@PathVariable Integer id, @Valid @RequestBody CicloLectivo ciclo) {
        return cicloService.actualizar(id, ciclo);
    }

    @PutMapping("/{id}/cerrar")
    public CicloLectivo cerrar(@PathVariable Integer id) {
        return cicloService.cerrar(id);
    }
}