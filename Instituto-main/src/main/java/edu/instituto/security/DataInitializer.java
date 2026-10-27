package edu.instituto.security;

import edu.instituto.model.Rol;
import edu.instituto.model.Usuario;
import edu.instituto.repository.RolRepository;
import edu.instituto.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Crea el primer ADMIN si la tabla usuario está vacía (si no, nadie podría loguearse nunca).
// La contraseña viene de la variable de entorno ADMIN_PASSWORD: no se escribe en el código ni en Git.
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           RolRepository rolRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${ADMIN_USERNAME:admin}") String adminUsername,
                           @Value("${ADMIN_PASSWORD:}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        // Los 4 roles tienen que existir siempre (carga idempotente).
        for (String nombre : Roles.TODOS) {
            if (rolRepository.findByNombre(nombre).isEmpty()) {
                Rol rol = new Rol();
                rol.setNombre(nombre);
                rolRepository.save(rol);
            }
        }
        if (usuarioRepository.count() > 0) {
            return;
        }
        if (adminPassword == null || adminPassword.length() < 8) {
            log.warn("No hay usuarios y falta ADMIN_PASSWORD (mínimo 8 caracteres): no se creó el administrador inicial.");
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombreUsuario(adminUsername);
        admin.setContrasena(passwordEncoder.encode(adminPassword));
        admin.setRol(rolRepository.findByNombre(Roles.ADMIN).orElseThrow());
        admin.setEstado("ACTIVO");
        usuarioRepository.save(admin);
        log.info("Administrador inicial '{}' creado.", adminUsername);
    }
}
