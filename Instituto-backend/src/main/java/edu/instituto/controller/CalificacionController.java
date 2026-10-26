
package edu.instituto.controller;

import edu.instituto.model.Calificacion;
import edu.instituto.service.CalificacionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    // Servicio encargado de la lógica de negocio.
    private final CalificacionService service;

    // Inyección de dependencias por constructor.
    public CalificacionController(CalificacionService service) {
        this.service = service;
    }

    // ==========================================
    // DTO: DATOS RECIBIDOS DESDE POSTMAN
    // ==========================================

    // Utilizamos un record para recibir el JSON.
    // BigDecimal permite representar notas decimales
    // sin los problemas de precisión de Double.
    public record CalificacionRequest(
            BigDecimal nota,
            Integer idEvaluacion,
            Integer idAlumno
    ) {}

    // ==========================================
    // GET: LISTAR TODAS LAS CALIFICACIONES
    // ==========================================

    // GET http://localhost:8080/api/calificaciones
    @GetMapping
    public ResponseEntity<List<Calificacion>> listar() {

        List<Calificacion> calificaciones = service.listar();

        return ResponseEntity.ok(calificaciones);
    }

    // ==========================================
    // GET: BUSCAR UNA CALIFICACIÓN POR ID
    // ==========================================

    // GET http://localhost:8080/api/calificaciones/1
    @GetMapping("/{id}")
    public ResponseEntity<Calificacion> buscar(
            @PathVariable Integer id) {

        Calificacion calificacion = service.buscar(id);

        return ResponseEntity.ok(calificacion);
    }

    // ==========================================
    // POST: CREAR UNA CALIFICACIÓN
    // ==========================================

    // POST http://localhost:8080/api/calificaciones
    @PostMapping
    public ResponseEntity<Calificacion> crear(
            @RequestBody CalificacionRequest request) {

        // El Controller recibe los datos.
        // El Service realiza todas las validaciones.
        Calificacion calificacion = service.crear(
                request.nota(),
                request.idEvaluacion(),
                request.idAlumno()
        );

        // HTTP 201: recurso creado correctamente.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(calificacion);
    }

    // ==========================================
    // PUT: ACTUALIZAR UNA CALIFICACIÓN
    // ==========================================

    // PUT http://localhost:8080/api/calificaciones/1
    @PutMapping("/{id}")
    public ResponseEntity<Calificacion> actualizar(
            @PathVariable Integer id,
            @RequestBody CalificacionRequest request) {

        // Se envía el ID de la calificación que
        // queremos modificar junto con los nuevos datos.
        Calificacion calificacion = service.actualizar(
                id,
                request.nota(),
                request.idEvaluacion(),
                request.idAlumno()
        );

        // HTTP 200: actualización correcta.
        return ResponseEntity.ok(calificacion);
    }

    // ==========================================
    // DELETE: ELIMINAR UNA CALIFICACIÓN
    // ==========================================

    // DELETE http://localhost:8080/api/calificaciones/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        service.eliminar(id);

        // HTTP 204: eliminación correcta,
        // sin devolver contenido en la respuesta.
        return ResponseEntity.noContent().build();
    }
}
