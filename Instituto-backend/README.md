# Instituto — backend integrado sin Lombok

Proyecto base recibido de la compañera, conservando sus módulos Alumno, Profesor, Materia y CicloLectivo, con getters/setters y constructores explícitos. Se añadieron Curso, CursoMateria, Inscripcion, Evaluacion y Calificacion.

## Importante antes de usar
- Configurar la conexión PostgreSQL en `src/main/resources/application.properties` usando variables de entorno. No incluir credenciales reales en Git.
- El proyecto usa Java 25 y el Spring Boot indicado en el `pom.xml` original. No se pudo ejecutar Maven en el entorno de preparación: compilar y probar localmente.
- Hacer copia de seguridad de `instituto_db` antes de aplicar cambios. Revisar nombres de columnas, restricciones y tipos contra el DER. No usar `ddl-auto=update` sobre una base compartida sin revisar migraciones.
- El proyecto original tenía SecurityConfig con `permitAll` y CSRF desactivado. **No es seguridad de producción**. Deben implementar autenticación/autorización antes de considerar completada la Entrega 3.
- `Curso` ahora tiene una relación real con CicloLectivo. Para crear un curso, el JSON mantiene `idCicloLectivo` como entrada, pero la respuesta muestra `cicloLectivo` anidado.
- `CursoMateria` usa `idCurso`, `idMateria`, `idProfesor` como entrada.
- `Inscripcion` usa `fechaInscripcion`, `estado`, `idAlumno`, `idCurso`.
- `Evaluacion` usa `fecha`, `tipo`, `descripcion`, `idCursoMateria`.
- `Calificacion` usa `nota`, `idEvaluacion`, `idAlumno`. El rango 0–10 es provisional y debe confirmarse.
- Las validaciones avanzadas de negocio (cupo, alumno inscrito, profesor activo, duplicados, estados, notas) están **pendientes**; los CRUD incorporados son una base de integración y no una entrega final verificada.
- DELETE de los cuatro módulos nuevos es físico; revisar reglas de historial y claves foráneas antes de usarlo con datos reales.

## Rutas
`/api/cursos`, `/api/curso-materias`, `/api/inscripciones`, `/api/evaluaciones`, `/api/calificaciones`.
