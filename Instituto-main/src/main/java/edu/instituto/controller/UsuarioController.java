package edu.instituto.controller;

import edu.instituto.model.Usuario;
import edu.instituto.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Solo ADMIN (configurado en SecurityConfig). Nunca se devuelve el hash de la contraseña.
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    public record Datos(String nombreUsuario, String contrasena, String rol, Integer idAlumno, Integer idProfesor) {}
    public record ModificarDatos(String rol, Integer idAlumno, Integer idProfesor) {}
    public record ContrasenaDatos(String contrasena) {}

    public record UsuarioRespuesta(Integer id, String nombreUsuario, String rol, String estado,
                                   Integer idAlumno, Integer idProfesor) {
        public static UsuarioRespuesta de(Usuario u) {
            return new UsuarioRespuesta(
                    u.getId(), u.getNombreUsuario(), u.getRol().getNombre(), u.getEstado(),
                    u.getAlumno() != null ? u.getAlumno().getId() : null,
                    u.getProfesor() != null ? u.getProfesor().getId() : null);
        }
    }

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioRespuesta> listar() {
        return usuarioService.listar().stream().map(UsuarioRespuesta::de).toList();
    }

    @GetMapping("/{id}")
    public UsuarioRespuesta buscar(@PathVariable Integer id) {
        return UsuarioRespuesta.de(usuarioService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRespuesta crear(@RequestBody Datos datos) {
        return UsuarioRespuesta.de(usuarioService.crear(
                datos.nombreUsuario(), datos.contrasena(), datos.rol(), datos.idAlumno(), datos.idProfesor()));
    }

    @PutMapping("/{id}")
    public UsuarioRespuesta actualizar(@PathVariable Integer id, @RequestBody ModificarDatos datos) {
        return UsuarioRespuesta.de(usuarioService.actualizar(id, datos.rol(), datos.idAlumno(), datos.idProfesor()));
    }

    @PutMapping("/{id}/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarContrasena(@PathVariable Integer id, @RequestBody ContrasenaDatos datos) {
        usuarioService.cambiarContrasena(id, datos.contrasena());
    }

    // Baja lógica.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Integer id, Authentication authentication) {
        usuarioService.darDeBaja(id, authentication.getName());
    }
}
