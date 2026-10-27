package edu.instituto.controller;

import edu.instituto.model.Pago;
import edu.instituto.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    public record Datos(LocalDate fechaPago, BigDecimal monto, String medioPago, String referencia, Integer idCuota) {}
    private final PagoService service;
    public PagoController(PagoService service) { this.service = service; }


    @GetMapping
    public List<Pago> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Pago buscar(@PathVariable Integer id) { return service.buscar(id); }

    @GetMapping("/cuota/{idCuota}")
    public List<Pago> porCuota(@PathVariable Integer idCuota) { return service.porCuota(idCuota); }

    @GetMapping("/alumno/{idAlumno}")
    public List<Pago> porAlumno(@PathVariable Integer idAlumno) { return service.porAlumno(idAlumno); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pago registrar(@RequestBody Datos datos) {
        return service.registrar(datos.fechaPago(), datos.monto(), datos.medioPago(), datos.referencia(), datos.idCuota());
    }
}
