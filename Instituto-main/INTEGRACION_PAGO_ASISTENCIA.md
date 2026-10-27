# Integración: Pago y Asistencia

Esta copia extiende el ZIP recibido; no se reemplazaron entidades preexistentes ni se cambió la API de Matricula. Se agregaron dos módulos y se actualizaron `CuotaService`, `CuotaRepository`, `SecurityConfig`.

## Antes de iniciar

- La copia recibida **no traía pom.xml**: se añadió uno de referencia con Spring Boot 3.5.7 y Java 21. Compararlo con el POM de la versión que ya funciona antes de usarlo. No se pudo compilar en este entorno.
- La configuración recibida apuntaba a PostgreSQL `localhost:5432/instituto`, puerto HTTP `8081`, **no** a `instituto_db` y `8080` de las conversaciones anteriores. Confirmar la DB real. No usar los comandos `TRUNCATE` de la versión anterior sin revisarlos.
- Por seguridad, `spring.datasource.password` usa `${DB_PASSWORD}` y se retiró la contraseña de administrador que venía escrita en el ZIP. Configurar `DB_PASSWORD` y `ADMIN_PASSWORD` como variables de entorno locales antes de ejecutar (y rotar las anteriores si se compartieron).
- `spring.jpa.hibernate.ddl-auto=update` puede crear tablas nuevas para **pruebas locales**; para la entrega final usar migraciones y realizar backup.
- El ZIP entrante ya usaba Lombok en varias clases del equipo, así que este módulo no lo necesita pero se mantiene esa dependencia.
- Observación: el ZIP recibido no incluye `CalificacionController` ni `CalificacionService` ni `CalificacionRepository`, aunque sí `Calificacion.java`. Consultar con el equipo antes de afirmar que ese módulo está integrado.

## Pago

- `POST /api/pagos` (ADMIN, ADMINISTRATIVO), JSON ejemplo:
  `{ "fechaPago": "2026-10-26", "monto": 15000.00, "medioPago": "TRANSFERENCIA", "referencia": "T-001", "idCuota": 1 }`
- `GET /api/pagos/1`
- `GET /api/pagos/cuota/1`
- `GET /api/pagos/alumno/1`

Cada pago es inmutable desde la API: no se expuso PUT/DELETE para mantener historial contable. Admite abonos parciales, verifica saldo mediante bloqueo de la cuota, rechaza sobrepagos, registra referencia opcional única y marca PAGADA cuando salda la cuota. Los pagos parciales reducen el total adeudado de `GET /api/cuotas/alumno/{id}/deuda`. Una cuota con pagos no puede anularse. No se agregaron reembolsos/anulaciones contables, conciliación o comprobantes descargables.

## Asistencia

- `POST /api/asistencias` (ADMIN o DOCENTE), JSON ejemplo:
  `{ "fecha": "2026-10-26", "estado": "PRESENTE", "observacion": "", "idInscripcion": 1 }`
- `GET /api/asistencias/{id}`
- `GET /api/asistencias/inscripcion/1`
- `GET /api/asistencias/curso/1?fecha=2026-10-26`
- `PUT /api/asistencias/{id}` (mismo JSON)
- `DELETE /api/asistencias/{id}`

Estados PRESENTE, AUSENTE, JUSTIFICADA; inscripción ACTIVA; fecha no futura, no anterior a inscripción ni fuera del ciclo; una fila por inscripción y fecha (regla también protegida por restricción única). Este diseño lleva asistencia **por curso**, no por asignación de materia; confirmarlo con el equipo.

## Pruebas recomendadas

1. Login `/api/auth/login` con sesión válida de administrador, conservar cookie de sesión en Postman.
2. Crear matrícula → generar cuota → registrar pago parcial → verificar deuda disminuida.
3. Registrar pago final → cuota PAGADA → intentar pagar de nuevo debe producir 409.
4. Crear inscripción ACTIVA → registrar asistencia del día → repetir el POST debe producir 409.
5. Probar IDs inexistentes y fechas inválidas (400/404).
6. Probar roles no autorizados (403).

No se ejecutaron pruebas automatizadas ni conexión a PostgreSQL en este entorno. Además, antes de presentar producción: evaluar protección CSRF al usar autenticación con cookies/sesiones, permisos de consulta para alumnos y auditoría de pagos.
