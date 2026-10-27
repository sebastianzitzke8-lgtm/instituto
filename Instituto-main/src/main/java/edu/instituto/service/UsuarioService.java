package edu.instituto.service;

import edu.instituto.model.Alumno;
import edu.instituto.model.Profesor;
import edu.instituto.model.Rol;
import edu.instituto.model.Usuario;
import edu.instituto.repository.AlumnoRepository;
import edu.instituto.repository.ProfesorRepository;
import edu.instituto.repository.RolRepository;
import edu.instituto.repository.UsuarioRepository;
import edu.instituto.security.Roles;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Reglas de los usuarios: alta, modificación, baja lógica, asignación de rol y cambio de contraseña (RF02).
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          AlumnoRepository alumnoRepository,
                          ProfesorRepository profesorRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.profesorRepository = profesorRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // RN17: el alumno solo ve lo suyo, así que el id del alumno sale de la sesión, nunca de la URL.
    public Integer idAlumnoDe(String nombreUsuario) {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);
        if (usuario.getAlumno() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está vinculado a un alumno");
        }
        return usuario.getAlumno().getId();
    }

    @Transactional
    public Usuario crear(String nombreUsuario, String contrasena, String rol, Integer idAlumno, Integer idProfesor) {
        String user = normalizarNombreUsuario(nombreUsuario);
        validarContrasena(contrasena);
        if (usuarioRepository.existsByNombreUsuario(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese nombre");
        }
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(user);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setEstado("ACTIVO");
        asignarRol(usuario, normalizarRol(rol), idAlumno, idProfesor);
        return usuarioRepository.save(usuario);
    }

    // Cambia el rol y/o la vinculación con alumno o profesor.
    @Transactional
    public Usuario actualizar(Integer id, String rol, Integer idAlumno, Integer idProfesor) {
        Usuario usuario = buscar(id);
        asignarRol(usuario, normalizarRol(rol), idAlumno, idProfesor);
        return usuarioRepository.save(usuario);
    }

    // Baja lógica: el usuario queda INACTIVO y ya no puede iniciar sesión.
    @Transactional
    public void darDeBaja(Integer id, String usernameActual) {
        Usuario usuario = buscar(id);
        if (usuario.getNombreUsuario().equals(usernameActual)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No podés dar de baja tu propio usuario");
        }
        usuario.setEstado("INACTIVO");
        usuarioRepository.save(usuario);
    }

    // El administrador resetea la contraseña de otro usuario.
    @Transactional
    public void cambiarContrasena(Integer id, String nueva) {
        validarContrasena(nueva);
        Usuario usuario = buscar(id);
        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    // Cada usuario cambia su propia contraseña (pide la actual).
    @Transactional
    public void cambiarContrasenaPropia(String nombreUsuario, String actual, String nueva) {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);
        if (actual == null || !passwordEncoder.matches(actual, usuario.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
        validarContrasena(nueva);
        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    // ---------- validaciones internas ----------

    private String normalizarNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario es obligatorio");
        }
        String user = nombreUsuario.trim();
        if (user.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario no puede superar 50 caracteres");
        }
        return user;
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres");
        }
        if (contrasena.length() > 72) { // límite de BCrypt
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede superar 72 caracteres");
        }
    }

    private String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }
        String normalizado = rol.trim().toUpperCase(Locale.ROOT);
        if (!Roles.TODOS.contains(normalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rol inválido. Valores permitidos: ADMIN, ADMINISTRATIVO, DOCENTE, ALUMNO");
        }
        return normalizado;
    }

    // ALUMNO exige idAlumno, DOCENTE exige idProfesor; ADMIN y ADMINISTRATIVO no llevan vínculo.
    private void asignarRol(Usuario usuario, String rol, Integer idAlumno, Integer idProfesor) {
        Alumno alumno = null;
        Profesor profesor = null;

        if (Roles.ALUMNO.equals(rol)) {
            if (idAlumno == null || idProfesor != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un usuario ALUMNO debe indicar solo idAlumno");
            }
            alumno = alumnoRepository.findById(idAlumno)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado"));
            if (!"ACTIVO".equalsIgnoreCase(alumno.getEstado())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno no está activo");
            }
            Optional<Usuario> otro = usuarioRepository.findByAlumno_Id(idAlumno);
            if (otro.isPresent() && !otro.get().getId().equals(usuario.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese alumno ya tiene un usuario");
            }
        } else if (Roles.DOCENTE.equals(rol)) {
            if (idProfesor == null || idAlumno != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un usuario DOCENTE debe indicar solo idProfesor");
            }
            profesor = profesorRepository.findById(idProfesor)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado"));
            if (!"ACTIVO".equalsIgnoreCase(profesor.getEstado())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El profesor no está activo");
            }
            Optional<Usuario> otro = usuarioRepository.findByProfesor_Id(idProfesor);
            if (otro.isPresent() && !otro.get().getId().equals(usuario.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese profesor ya tiene un usuario");
            }
        } else if (idAlumno != null || idProfesor != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los roles ADMIN y ADMINISTRATIVO no se vinculan a alumno ni profesor");
        }

        // El rol se busca en la tabla rol (si no existe, el nombre es inválido).
        Rol rolEntidad = rolRepository.findByNombre(rol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inexistente: " + rol));
        usuario.setRol(rolEntidad);
        usuario.setAlumno(alumno);
        usuario.setProfesor(profesor);
    }
}
