package edu.instituto.security;

import java.util.Set;

// Roles del sistema (RF03). Spring les agrega el prefijo ROLE_ automáticamente.
public final class Roles {
    public static final String ADMIN = "ADMIN";
    public static final String ADMINISTRATIVO = "ADMINISTRATIVO";
    public static final String DOCENTE = "DOCENTE";
    public static final String ALUMNO = "ALUMNO";
    public static final Set<String> TODOS = Set.of(ADMIN, ADMINISTRATIVO, DOCENTE, ALUMNO);

    private Roles() {}
}
