# IGNACIO — Lo que debes implementar en el Backend

El frontend está listo y preparado para conectarse. Abajo está todo lo que el backend necesita soportar.

---

## 1. Columna nueva en la tabla `usuario`

Agrega la columna `imagen_base64` de tipo `TEXT` (es un string base64 de la imagen de perfil).

```sql
ALTER TABLE usuario ADD COLUMN imagen_base64 TEXT;
```

Actualiza la entidad Java/Spring:

```java
@Column(columnDefinition = "TEXT")
private String imagenBase64;
```

---

## 2. Endpoint de Login

**Actualmente el frontend usa un workaround** (`GET /api/v1/usuarios`) para hacer login.  
**Debes reemplazarlo** con un endpoint real:

```
POST /api/v1/auth/login
```

**Body esperado:**
```json
{
  "username": "pepito",
  "password": "1234"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "id": 1,
  "username": "pepito",
  "rol": "admin",
  "imagenBase64": "base64string..."
}
```

**Si las credenciales son incorrectas → 401 Unauthorized.**

> Cuando implementes este endpoint, cambia en `AuthService` (frontend) el método `login()` para que use `POST /api/v1/auth/login` en lugar del `GET /api/v1/usuarios`.

---

## 3. Endpoint de Registro

```
POST /api/v1/auth/register
```

**Body esperado:**
```json
{
  "username": "pepito",
  "password": "1234",
  "rol": "admin",
  "imagenBase64": "base64string..."
}
```

**Lógica del backend:**
1. Crear el registro en la tabla `usuario`.
2. **Si `rol == "admin"`:** crear automáticamente un registro en la tabla `profesional` con:
   - `nombre_completo` = `username`
   - `imagen_base64` = la imagen subida
   - `servicio_id` = el que viene en el body (ver punto 4)
3. Devolver el usuario creado (sin password).

**Respuesta esperada (201 Created):**
```json
{
  "id": 5,
  "username": "pepito",
  "rol": "admin",
  "imagenBase64": "base64string..."
}
```

---

## 4. Registro de admin con especialidad

Cuando un usuario se registra como `admin` (profesional), el frontend también envía un POST a `/api/v1/profesionales` con:

```json
{
  "nombreCompleto": "pepito",
  "imagenBase64": "base64string...",
  "servicio": { "id": 2, "nombre": "Ortodoncia" }
}
```

**Asegúrate que el endpoint `POST /api/v1/profesionales` acepte `servicio` con solo el `id` (sin necesidad de enviar el objeto completo).**  
El campo `servicio_id` en la tabla `profesional` puede ser **nullable** por si el profesional aún no tiene especialidad asignada.

```sql
ALTER TABLE profesional ALTER COLUMN servicio_id DROP NOT NULL;
```

---

## 5. Campo `imagen_base64` en `usuario` — respuesta de `GET /api/v1/usuarios`

Actualmente el frontend usa `GET /api/v1/usuarios` temporalmente para autenticar.  
Cuando el usuario se loguea, el frontend guarda el objeto devuelto en `localStorage`.  
**Asegúrate de que `GET /api/v1/usuarios` y `GET /api/v1/usuarios/{id}` devuelvan el campo `imagenBase64`.**

> ⚠️ No devuelvas el campo `password` en ningún GET. Ponle `@JsonIgnore` en la entidad.

---

## 6. Resumen de Endpoints que ya deben funcionar (verificar)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/servicios` | Listar todos los servicios |
| POST | `/api/v1/servicios` | Crear un servicio (solo admin) |
| GET | `/api/v1/profesionales` | Listar todos los profesionales |
| POST | `/api/v1/profesionales` | Crear un profesional |
| GET | `/api/v1/pacientes` | Listar pacientes |
| POST | `/api/v1/pacientes` | Crear paciente |
| POST | `/api/v1/turnos` | Crear turno |
| GET | `/api/v1/usuarios` | Listar usuarios (temporal para login) |
| POST | `/api/v1/usuarios` | Crear usuario (registro actual) |

---

## 7. Gestión de Turnos y Disponibilidad (Sincronización necesaria)

Para que el formulario de "Pedir Turno" sea profesional, necesitamos que los campos coincidan con el modelo real y que la disponibilidad sea dinámica:

1. **Sincronización de Entidades:**
   - **PacienteEntity:** Asegúrate de que tenga `nombreCompleto`, `rut`, `telefono` y `email`.
   - **TurnoEntity:** Debe tener `fecha` (DATE), `horario` (String), `mensajeAdicional` (TEXT), `paciente` (FK) y `servicio` (FK). El frontend enviará objetos completos para `paciente` y `servicio`, asegúrate de que el JSON se mapee bien.

2. **Endpoint de Horarios Disponibles (PENDIENTE):**
   - Actualmente el frontend usa horarios estáticos ("Mañana", "Tarde"). Necesitamos un endpoint:
     `GET /api/v1/turnos/disponibles?servicioId=1&fecha=2024-05-10`
   - Este endpoint debería devolver un array de strings con las horas disponibles (ej: `["09:00", "09:30", "10:00"]`).
   - El backend debe validar que la fecha no sea pasada ni domingo.

3. **Validación de Datos:**
   - No permitas crear turnos si el `Paciente` no tiene el `rut` o `email` bien formateado.

---

## 8. CORS — URGENTE (el backend actualmente da 403 en OPTIONS)

El backend rechaza preflight requests del browser. Mientras tanto el frontend usa un proxy local que evita el problema en desarrollo, pero **en producción necesitas CORS configurado**.

Agrega esto en Spring Boot (en la clase principal o en un `@Configuration`):

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
        }
    };
}
```

O con `@CrossOrigin(origins = "*")` en cada controller, o globalmente en Spring Security.

---

## 8. Orden sugerido para implementar

1. Agregar columna `imagen_base64` a `usuario`
2. Hacer que `GET /api/v1/usuarios` devuelva `imagenBase64` (y NO devuelva `password`)
3. Hacer `servicio_id` nullable en `profesional`
4. Implementar `POST /api/v1/auth/login`
5. Implementar `POST /api/v1/auth/register`
6. Avisar a SubaruDev0 para que actualice el `AuthService` del frontend al endpoint real

---

> El frontend ya está completamente listo. Solo necesita que el backend responda correctamente a estos endpoints.
