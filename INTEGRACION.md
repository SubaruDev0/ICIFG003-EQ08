# Documentación de Integración FullStack

Clínica Dental — Proyecto ICIFG003-EQ08

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Frontend | Angular 17 (standalone components) |
| Backend | Spring Boot 2.5.9 (Java 17, Maven) |
| Base de datos | PostgreSQL en [Neon](https://neon.tech) (cloud) |
| Comunicación | REST API con JSON sobre HTTP |

---

## Estructura del proyecto

```
ICIFG003-EQ08/
├── Frontend/          # Aplicación Angular 17
├── Backend/           # API REST Spring Boot
├── .env               # Variables de entorno (NO subir a git)
├── .env.example       # Plantilla de variables (sí subir)
└── INTEGRACION.md     # Este archivo
```

---

## Variables de entorno

El Backend usa variables de entorno para no guardar credenciales en el código.

**Crear el archivo `.env` en la raíz del proyecto** (ya está en `.gitignore`):

```env
DB_URL=jdbc:postgresql://ep-XXXXXX.us-east-2.aws.neon.tech/clinica_dental?sslmode=require
DB_USERNAME=tu_usuario_neon
DB_PASSWORD=tu_password_neon
PORT=6789
```

> Los valores reales de Neon los provee Ignacio. El `.env` nunca se sube a GitHub.

Spring Boot los lee así en `application.properties`:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## Entidades del sistema

Son los "modelos de datos" que existen tanto en el Backend (Java) como en el Frontend (TypeScript).

### Usuario
Representa a una persona con acceso al sistema (login).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | number | Identificador único (lo genera la BD) |
| username | string | Nombre de usuario para login |
| password | string | Contraseña |
| rol | string | Rol del usuario (ej: "ADMIN", "PACIENTE") |

### Paciente
Persona que solicita un turno en la clínica.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | number | Identificador único |
| nombreCompleto | string | Nombre completo del paciente |
| rut | string | RUT chileno (ej: 12.345.678-9) |
| telefono | string | Teléfono de contacto |
| email | string | Correo electrónico |
| usuario | Usuario | (Opcional) cuenta de usuario asociada |

### Servicio
Un tipo de tratamiento dental que ofrece la clínica.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | number | Identificador único |
| nombre | string | Nombre del servicio (ej: "Ortodoncia") |

### Profesional
Un dentista o especialista de la clínica.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | number | Identificador único |
| nombreCompleto | string | Nombre del profesional |
| imagenBase64 | string | Foto codificada en Base64 |
| servicio | Servicio | Especialidad que ejerce |

### Turno
Una cita agendada por un paciente.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | number | Identificador único |
| fecha | string | Fecha de la cita (formato YYYY-MM-DD) |
| horario | string | Bloque horario (ej: "mañana", "tarde") |
| mensajeAdicional | string | Observaciones del paciente |
| paciente | Paciente | Paciente que agenda |
| servicio | Servicio | Servicio solicitado |

---

## Endpoints del Backend (API REST)

El Backend corre en `http://localhost:6789`.

| Entidad | GET todos | GET por ID | POST crear | PUT actualizar | DELETE |
|---------|-----------|------------|------------|----------------|--------|
| Usuarios | `GET /api/v1/usuarios` | `GET /api/v1/usuarios/{id}` | `POST /api/v1/usuarios` | `PUT /api/v1/usuarios/{id}` | `DELETE /api/v1/usuarios/{id}` |
| Pacientes | `GET /api/v1/pacientes` | `GET /api/v1/pacientes/{id}` | `POST /api/v1/pacientes` | `PUT /api/v1/pacientes/{id}` | `DELETE /api/v1/pacientes/{id}` |
| Servicios | `GET /api/v1/servicios` | `GET /api/v1/servicios/{id}` | `POST /api/v1/servicios` | `PUT /api/v1/servicios/{id}` | `DELETE /api/v1/servicios/{id}` |
| Profesionales | `GET /api/v1/profesionales` | `GET /api/v1/profesionales/{id}` | `POST /api/v1/profesionales` | `PUT /api/v1/profesionales/{id}` | `DELETE /api/v1/profesionales/{id}` |
| Turnos | `GET /api/v1/turnos` | `GET /api/v1/turnos/{id}` | `POST /api/v1/turnos` | `PUT /api/v1/turnos/{id}` | `DELETE /api/v1/turnos/{id}` |

---

## Archivos de integración del Frontend

### Modelos TypeScript (`Frontend/src/app/models/`)
Equivalentes a las entidades Java. Definen la forma del dato en TypeScript.

```
models/
├── usuario.model.ts
├── paciente.model.ts
├── servicio.model.ts
├── profesional.model.ts
└── turno.model.ts
```

### Servicios Angular (`Frontend/src/app/services/`)
Cada servicio maneja las llamadas HTTP a un endpoint del backend.

```
services/
├── usuario.service.ts    → /api/v1/usuarios
├── paciente.service.ts   → /api/v1/pacientes
├── servicio.service.ts   → /api/v1/servicios
├── profesional.service.ts → /api/v1/profesionales
└── turno.service.ts      → /api/v1/turnos
```

### Archivos de entorno (`Frontend/src/environments/`)
Configuran la URL base del Backend según el ambiente.

- `environment.ts` → desarrollo local (`http://localhost:6789`)
- `environment.prod.ts` → producción (URL de Neon/deploy)

---

## Qué hace cada página con el backend

| Página | Acción |
|--------|--------|
| **Login** | Llama `GET /api/v1/usuarios` y verifica credenciales |
| **Equipo** | Llama `GET /api/v1/profesionales` y renderiza las tarjetas dinámicamente |
| **Pedir Turno** | Llama `GET /api/v1/servicios` para el select; al enviar crea un `Paciente` y luego un `Turno` |
| **Servicios** | Página estática (las cards con imágenes son decorativas) |
| **Inicio / Contacto** | Páginas estáticas, sin conexión al backend |

---

## Cómo levantar el proyecto

### Backend
```bash
cd Backend

# Asegúrate de tener el .env en la raíz y exportar las variables:
export $(cat ../.env | xargs)

./mvnw spring-boot:run
# Corre en http://localhost:6789
```

### Frontend
```bash
cd Frontend
npm install
ng serve
# Corre en http://localhost:4200
```

---

## Notas importantes

- **CORS**: El Backend tiene `@CrossOrigin(origins = "http://localhost:4200")` en todos los controladores, lo que permite que Angular lo consuma localmente.
- **Base de datos Neon**: Es PostgreSQL en la nube. La URL de conexión tiene el parámetro `?sslmode=require` obligatorio para Neon.
- **Login**: La autenticación actual es simple (compara username y password directamente). Para producción se debería implementar JWT o Spring Security.
- **Imágenes de profesionales**: Se almacenan como texto Base64 en la BD. Para agregar un profesional, se debe convertir la imagen a Base64 antes de enviarlo.
