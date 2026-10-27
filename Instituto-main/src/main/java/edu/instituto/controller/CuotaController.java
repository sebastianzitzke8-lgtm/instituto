package edu.instituto.controller;

import edu.instituto.model.Cuota;
import edu.instituto.service.CuotaService;
import edu.instituto.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cuotas")
public class CuotaController {

    public record GenerarDatos(Integer idMatricula, Integer cantidad, BigDecimal importe, LocalDate primerVencimiento) {}

    private final CuotaService service;
    private final UsuarioService usuarioService;

    public CuotaController(CuotaService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    // Vistas del alumno logueado (RF29, RN17).
    @GetMapping("/mis-cuotas")
    public List<Cuota> misCuotas(Authentication authentication) {
        return service.listarPorAlumno(usuarioService.idAlumnoDe(authentication.getName()));
    }

    @GetMapping("/mi-deuda")
    public CuotaService.Deuda miDeuda(Authentication authentication) {
        return service.deuda(usuarioService.idAlumnoDe(authentication.getName()));
    }

    @GetMapping("/{id}")
    public Cuota buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @GetMapping("/matricula/{idMatricula}")
    public List<Cuota> porMatricula(@PathVariable Integer idMatricula) {
        return service.listarPorMatricula(idMatricula);
    }

    @GetMapping("/alumno/{idAlumno}")
    public List<Cuota> porAlumno(@PathVariable Integer idAlumno) {
        return service.listarPorAlumno(idAlumno);
    }

    @GetMapping("/alumno/{idAlumno}/deuda")
    public CuotaService.Deuda deuda(@PathVariable Integer idAlumno) {
        return service.deuda(idAlumno);
    }

    @PostMapping("/generar")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Cuota> generar(@RequestBody GenerarDatos datos) {
        return service.generar(datos.idMatricula(), datos.cantidad(), datos.importe(), datos.primerVencimiento());
    }

    @PatchMapping("/{id}/anular")
    public Cuota anular(@PathVariable Integer id) {
        return service.anular(id);
    }
}
