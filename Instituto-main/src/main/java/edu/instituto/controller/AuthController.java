package edu.instituto.controller;

import edu.instituto.model.Usuario;
import edu.instituto.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// RF01: autenticación con usuario y contraseña. La sesión viaja en una cookie (JSESSIONID).
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginDatos(String nombreUsuario, String contrasena) {}
    public record PasswordDatos(String contrasenaActual, String contrasenaNueva) {}

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public UsuarioController.UsuarioRespuesta login(@RequestBody LoginDatos datos,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        if (datos == null || datos.nombreUsuario() == null || datos.nombreUsuario().isBlank()
                || datos.contrasena() == null || datos.contrasena().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario y contraseña son obligatorios");
        }
        try {
            Authentication autenticado = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(datos.nombreUsuario().trim(), datos.contrasena()));

            // Evita fijación de sesión: si ya había una sesión, se le cambia el id.
            if (request.getSession(false) != null) {
                request.changeSessionId();
            }
            SecurityContext contexto = SecurityContextHolder.createEmptyContext();
            contexto.setAuthentication(autenticado);
            SecurityContextHolder.setContext(contexto);
            securityContextRepository.saveContext(contexto, request, response);

            return UsuarioController.UsuarioRespuesta.de(usuarioService.buscarPorNombreUsuario(autenticado.getName()));
        } catch (AuthenticationException e) {
            // Mismo mensaje para usuario inexistente, clave incorrecta o usuario dado de baja.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
    }

    // Devuelve quién está logueado (el frontend lo usa para mostrar el menú según el rol).
    @GetMapping("/me")
    public UsuarioController.UsuarioRespuesta me(Authentication authentication) {
        Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
        return UsuarioController.UsuarioRespuesta.de(usuario);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
    }

    @PutMapping("/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarContrasena(@RequestBody PasswordDatos datos, Authentication authentication) {
        usuarioService.cambiarContrasenaPropia(authentication.getName(), datos.contrasenaActual(), datos.contrasenaNueva());
    }
}
