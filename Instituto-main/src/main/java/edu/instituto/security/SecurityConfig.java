package edu.instituto.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static edu.instituto.security.Roles.*;

// Autenticación por sesión (login con usuario y contraseña) + autorización por rol (RF01, RF03, RNF01, RNF02).
@Configuration
public class SecurityConfig {

    // RNF01: las contraseñas se guardan con hash BCrypt, nunca en texto plano.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF desactivado: el frontend consume una API REST con JSON. Revisar si el frontend se sirve desde otro origen.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            // Sin sesión devuelve 401 (en vez de redirigir a un formulario HTML).
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(a -> a
                // --- Login / sesión ---
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/**").authenticated()

                // --- Usuarios: solo el administrador (RF02) ---
                .requestMatchers("/api/usuarios/**").hasRole(ADMIN)

                // --- Matrículas y cuotas ---
                // El alumno solo ve lo suyo (RN17, RF29): estos endpoints toman el alumno desde la sesión.
                .requestMatchers(HttpMethod.GET, "/api/matriculas/mis-matriculas",
                        "/api/cuotas/mis-cuotas", "/api/cuotas/mi-deuda").hasRole(ALUMNO)
                .requestMatchers("/api/matriculas/**", "/api/cuotas/**").hasAnyRole(ADMIN, ADMINISTRATIVO)

                // --- Cobros: consulta de pagos y registro administrativo ---
                .requestMatchers(HttpMethod.GET, "/api/pagos/**").hasAnyRole(ADMIN, ADMINISTRATIVO)
                .requestMatchers("/api/pagos/**").hasAnyRole(ADMIN, ADMINISTRATIVO)

                // --- Asistencias: docentes registran; administración consulta ---
                .requestMatchers(HttpMethod.GET, "/api/asistencias/**").hasAnyRole(ADMIN, ADMINISTRATIVO, DOCENTE)
                .requestMatchers("/api/asistencias/**").hasAnyRole(ADMIN, DOCENTE)

                // --- Módulos académicos ya existentes ---
                .requestMatchers(HttpMethod.GET, "/api/alumnos/**", "/api/profesores/**")
                    .hasAnyRole(ADMIN, ADMINISTRATIVO, DOCENTE)
                .requestMatchers("/api/alumnos/**").hasAnyRole(ADMIN, ADMINISTRATIVO)
                .requestMatchers("/api/profesores/**").hasRole(ADMIN)

                .requestMatchers(HttpMethod.GET, "/api/materias/**", "/api/ciclos-lectivos/**",
                        "/api/cursos/**", "/api/curso-materias/**").authenticated()
                .requestMatchers("/api/materias/**", "/api/ciclos-lectivos/**",
                        "/api/cursos/**", "/api/curso-materias/**").hasRole(ADMIN)

                .requestMatchers(HttpMethod.GET, "/api/inscripciones/**").hasAnyRole(ADMIN, ADMINISTRATIVO, DOCENTE)
                .requestMatchers("/api/inscripciones/**").hasAnyRole(ADMIN, ADMINISTRATIVO)

                .requestMatchers(HttpMethod.GET, "/api/evaluaciones/**", "/api/calificaciones/**")
                    .hasAnyRole(ADMIN, DOCENTE)
                .requestMatchers("/api/evaluaciones/**", "/api/calificaciones/**").hasRole(DOCENTE)

                // Todo lo que no esté listado arriba requiere estar logueado.
                .anyRequest().authenticated());
        return http.build();
    }
}
