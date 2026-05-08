# Documentación de Arquitectura — ICIFG003-EQ08 Clínica Dental

> Documento técnico para revisión académica. Explica las decisiones de diseño, estructura de carpetas y funcionamiento del proyecto.

---

## Índice

1. [Visión general del proyecto](#1-visión-general-del-proyecto)
2. [Estructura del Frontend (Angular)](#2-estructura-del-frontend-angular)
   - [¿Por qué core / shared / features?](#por-qué-core--shared--features)
   - [¿Qué hay en models, pages y services?](#qué-hay-en-models-pages-y-services)
   - [Archivos fuera de esa estructura](#archivos-fuera-de-esa-estructura)
3. [Estructura del Backend (Spring Boot)](#3-estructura-del-backend-spring-boot)
   - [¿Dónde se crean las tablas?](#dónde-se-crean-las-tablas)
   - [ORM vs SQL crudo](#orm-vs-sql-crudo)
   - [¿Qué hace cada capa?](#qué-hace-cada-capa)
4. [Flujo completo de una petición](#4-flujo-completo-de-una-petición)
5. [Base de datos y relaciones](#5-base-de-datos-y-relaciones)
6. [Decisiones técnicas relevantes](#6-decisiones-técnicas-relevantes)

---

## 1. Visión general del proyecto

El proyecto es una aplicación web para gestión de turnos de una clínica dental. Tiene dos partes completamente separadas:

| Parte | Tecnología | Puerto local | URL producción |
|---|---|---|---|
| Frontend | Angular 17 (standalone) | 4200 | https://icifg003-eq08.onrender.com |
| Backend | Spring Boot 2.5 + JPA | 6789 | https://icifg003-eq08-back.onrender.com |
| Base de datos | PostgreSQL (Neon cloud) | — | Neon SA-East-1 |

El frontend **nunca accede directo a la base de datos**. Siempre llama al backend por HTTP (`/api/v1/...`), y el backend es el único que habla con la base de datos.

---

## 2. Estructura del Frontend (Angular)

```
Frontend/src/app/
├── core/                  ← Servicios globales (singleton)
│   ├── services/
│   │   └── auth.service.ts
│   └── interceptors/
├── shared/                ← Componentes reutilizables en toda la app
│   └── components/
│       ├── header.component.ts / .html / .css
│       └── footer.component.ts / .html / .css
├── features/              ← Módulos funcionales por dominio
│   ├── auth/              (login, registro)
│   ├── inicio/            (página de inicio)
│   ├── servicios/         (catálogo de servicios dentales)
│   ├── profesionales/     (equipo profesional)
│   ├── turnos/            (pedir turno, gestionar turnos)
│   ├── pacientes/
│   └── contacto/
├── app.routes.ts          ← Definición de rutas de la SPA
├── app.component.ts/html  ← Componente raíz (shell)
└── app.config.ts          ← Configuración global de Angular
```

### ¿Por qué core / shared / features?

Esta división sigue el patrón **"Feature Modules"** (o en Angular 17 standalone, su equivalente conceptual). La idea es separar responsabilidades:

- **`core/`**: todo lo que debe existir **una sola vez** en la app y ser inyectado globalmente. Ejemplo: `AuthService` maneja la sesión del usuario. Si hubiera múltiples instancias, cada componente tendría un estado diferente del usuario logueado, lo cual es un bug. Al estar en `core`, Angular lo provee como singleton (una sola instancia).

- **`shared/`**: componentes que **no pertenecen a ningún feature específico** pero se usan en muchos lados. El `HeaderComponent` y `FooterComponent` los usa `inicio`, `servicios`, `turnos`, etc. Si cada feature tuviera su propio header, habría 6 copias del mismo código. `shared` evita esa duplicación.

- **`features/`**: cada subcarpeta es un **dominio de negocio independiente**. `turnos/` solo sabe de turnos. `servicios/` solo sabe de servicios. Esto facilita encontrar código (si hay un bug en el formulario de pedir turno, voy a `features/turnos/pages/`) y también que distintos integrantes del equipo trabajen en distintos features sin pisarse.

### ¿Qué hay en models, pages y services?

Dentro de cada feature se repite la misma subdivisión:

#### `models/`
Define la **forma de los datos** (TypeScript interfaces). No contiene lógica, solo tipos.

```typescript
// features/turnos/models/turno.model.ts
export interface Turno {
  id?: number;
  fecha: string;
  horario: string;
  estado?: string;
  paciente?: any;
  servicio?: any;
}
```

**¿Para qué sirve?** Para que TypeScript sepa qué propiedades tiene un objeto. Si en algún lado intentamos acceder a `turno.horarioMal` (que no existe), el compilador lo detecta antes de correr la app. También sirve como documentación: al ver el modelo sabemos exactamente qué campos espera el backend.

#### `pages/`
Son los **componentes que corresponden a una ruta/pantalla**. Cada archivo `.ts` tiene su template `.html` y estilos `.css`.

```
pedir-turno.component.ts   ← lógica: carga servicios, horarios, valida y envía
pedir-turno.component.html ← template: el formulario que ve el usuario
pedir-turno.component.css  ← estilos propios de esa pantalla
```

**¿Por qué separarlos?** El `.ts` maneja el **cómo** (lógica de negocio), el `.html` maneja el **qué** (vista) y el `.css` el **aspecto visual**. Esto se llama separación de responsabilidades (SoC). Cambiar el diseño no toca la lógica y viceversa.

#### `services/`
Son clases que encapsulan las **llamadas HTTP al backend**. Los componentes (pages) no deberían saber la URL del endpoint, solo llaman al servicio.

```typescript
// features/servicios/services/servicio.service.ts
@Injectable({ providedIn: 'root' })
export class ServicioService {
  private url = `${environment.apiUrl}/api/v1/servicios`;

  findAll(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.url);
  }
}
```

**¿Por qué separarlos de las pages?** Si la URL del backend cambia, solo cambia en el service. Si el componente hiciera el `http.get` directamente, habría que cambiar en 10 lugares. Además, el service se puede reutilizar en múltiples páginas (por ejemplo, `pedir-turno` y `crear-servicio` ambos usan `ServicioService.findAll()`).

---

### Archivos fuera de esa estructura

#### `app.component.ts / .html / .css / .spec.ts`

Son el **componente raíz** de la aplicación. Angular necesita un punto de entrada. `app.component.html` solo contiene `<router-outlet>`, que es el contenedor donde Angular renderiza el componente correspondiente a la ruta activa.

```html
<!-- app.component.html -->
<router-outlet></router-outlet>
```

Cuando el usuario navega a `/servicios`, Angular reemplaza `<router-outlet>` con `ServiciosComponent`. Es como un "slot" dinámico.

El archivo `.spec.ts` es el **test unitario** del componente (generado automáticamente por Angular CLI). En este proyecto no se escribieron tests específicos, pero el archivo existe como plantilla.

#### `app.routes.ts`

Define el **mapa de navegación** de la SPA (Single Page Application). Relaciona cada URL con su componente:

```typescript
{ path: 'servicios', component: ServiciosComponent },
{ path: 'pedir-turno', component: PedirTurnoComponent },
{ path: '**', redirectTo: 'login' }   // cualquier ruta no definida → login
```

¿Por qué no está dentro de `features/`? Porque las rutas son **transversales** a todos los features. Si estuvieran dentro de `features/turnos/`, no podría ver las rutas de `features/servicios/` sin importar ese módulo.

#### `app.config.ts`

Configura Angular a nivel global: qué proveedores (providers) están disponibles, si se usan animaciones, cómo se maneja el routing. En Angular 17 standalone reemplaza lo que antes iba en `AppModule`.

#### `environments/`

Contiene las configuraciones por entorno:

```typescript
// environment.ts (desarrollo)
export const environment = { production: false, apiUrl: '' };

// environment.prod.ts (producción)
export const environment = { production: true, apiUrl: 'https://icifg003-eq08-back.onrender.com' };
```

En desarrollo `apiUrl` es vacío porque el proxy de Angular (`proxy.conf.json`) redirige `/api` al backend local. En producción, el frontend llama directamente a la URL de Render.

#### `proxy.conf.json`

Solo se usa en desarrollo local. Le dice al servidor de Angular CLI que cuando el frontend llame a `/api/...`, lo reenvíe a `http://localhost:6789`. Esto evita el problema de CORS en local (el navegador ve que todo viene del mismo origen `localhost:4200`).

---

## 3. Estructura del Backend (Spring Boot)

```
Backend/src/main/java/com/example/demo/
├── config/          ← Configuración de Spring (CORS, seed de datos)
├── controller/      ← Recibe peticiones HTTP y devuelve respuestas
├── dto/             ← Objetos de transferencia de datos (lo que llega del frontend)
├── entity/          ← Clases que mapean a tablas de la base de datos
│   └── enums/       ← Tipos enumerados (estados, roles)
├── interfaces/      ← Contratos (interfaces) que implementan los services
├── repository/      ← Acceso a base de datos mediante JPA
├── service/         ← Lógica de negocio
└── DemoApplication.java  ← Punto de entrada (main)
```

### ¿Dónde se crean las tablas?

Las tablas **se crean automáticamente** gracias a JPA + Hibernate. No hay ningún archivo SQL de creación de tablas.

En `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

Con `ddl-auto=update`, Hibernate lee las clases `@Entity` y **genera o actualiza** las tablas en PostgreSQL automáticamente al iniciar la aplicación. Si agrego un campo nuevo a una entidad, Hibernate agrega la columna en la tabla sin perder los datos existentes.

Las clases `@Entity` están en el paquete `entity/`. Ejemplo:

```java
// entity/TurnoEntity.java
@Entity
@Table(name = "turno")          // ← nombre de la tabla en PostgreSQL
public class TurnoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // ← columna "id" autoincremental

    @Temporal(TemporalType.DATE)
    private Date fecha;          // ← columna "fecha" (tipo DATE en SQL)

    private String horario;      // ← columna "horario" (VARCHAR)

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private PacienteEntity paciente;  // ← foreign key → tabla "paciente"

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private ServicioEntity servicio;  // ← foreign key → tabla "servicio"
}
```

Hibernate traduce esto a SQL equivalente a:
```sql
CREATE TABLE turno (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE,
    horario VARCHAR(255),
    paciente_id BIGINT REFERENCES paciente(id),
    servicio_id BIGINT REFERENCES servicio(id)
);
```

### ORM vs SQL crudo

El proyecto usa **JPA/Hibernate como ORM** (Object-Relational Mapper). Esto significa que en lugar de escribir SQL manualmente, trabajamos con objetos Java y el ORM lo traduce.

**¿Por qué ORM y no SQL directo?**

| ORM (JPA/Hibernate) | SQL crudo (JDBC puro) |
|---|---|
| `pacienteRepository.findByRut("12345678-9")` | `"SELECT * FROM paciente WHERE rut = ?"` |
| Portable entre bases de datos | Atado a la sintaxis de cada BD |
| Menos propenso a SQL injection | Requiere cuidado extra con parámetros |
| Las relaciones entre tablas son objetos Java | Se manejan con JOINs manuales |
| Código más legible y mantenible | Más control de rendimiento |

El ORM es más limpio para un proyecto académico y de tamaño medio. En proyectos con consultas muy complejas o que requieren máximo rendimiento se puede combinar con consultas nativas.

### ¿Qué consultas se hacen a la BD y en qué archivos?

Todas las consultas pasan por los archivos en `repository/`. Spring Data JPA genera las consultas SQL automáticamente a partir del **nombre del método**:

```java
// repository/TurnoRepository.java
public interface TurnoRepository extends CrudRepository<TurnoEntity, Long> {

    // Spring genera: SELECT * FROM turno WHERE servicio_id = ? AND fecha = ?
    List<TurnoEntity> findByServicioIdAndFecha(Long servicioId, Date fecha);
}

// repository/PacienteRepository.java
public interface PacienteRepository extends CrudRepository<PacienteEntity, Long> {

    // Spring genera: SELECT * FROM paciente WHERE rut = ? LIMIT 1
    Optional<PacienteEntity> findByRut(String rut);
}
```

Los métodos heredados de `CrudRepository` cubren las operaciones básicas sin escribir nada:

| Método heredado | SQL generado |
|---|---|
| `repository.findAll()` | `SELECT * FROM tabla` |
| `repository.findById(id)` | `SELECT * FROM tabla WHERE id = ?` |
| `repository.save(entity)` | `INSERT INTO tabla (...)` o `UPDATE tabla SET ...` |
| `repository.deleteById(id)` | `DELETE FROM tabla WHERE id = ?` |
| `repository.count()` | `SELECT COUNT(*) FROM tabla` |
| `repository.deleteAll()` | `DELETE FROM tabla` |

### ¿Qué hace cada capa?

```
HTTP Request
     │
     ▼
┌─────────────┐
│ Controller  │  Recibe la request HTTP, valida parámetros básicos,
│             │  llama al service, devuelve ResponseEntity con código HTTP.
└──────┬──────┘
       │ llama a
       ▼
┌─────────────┐
│  Interface  │  Define el contrato del service (qué métodos existen).
│ (ITurno...) │  Permite cambiar la implementación sin tocar el controller.
└──────┬──────┘
       │ implementada por
       ▼
┌─────────────┐
│   Service   │  Contiene la lógica de negocio. Ejemplo: verificar que
│             │  el horario esté disponible antes de guardar un turno.
│             │  Llama al repository para acceder a la BD.
└──────┬──────┘
       │ llama a
       ▼
┌─────────────┐
│ Repository  │  Interfaz JPA. Spring genera las consultas SQL
│             │  automáticamente según el nombre del método.
└──────┬──────┘
       │ consulta
       ▼
┌─────────────┐
│ PostgreSQL  │  Base de datos en Neon (cloud). Las tablas las
│  (Neon)     │  creó Hibernate al arrancar la app por primera vez.
└─────────────┘
```

**Ejemplo concreto — pedir un turno:**

1. Frontend llama `POST /api/v1/turnos` con JSON `{fecha, horario, paciente, servicio}`
2. `TurnoController.save()` recibe la request, valida que vengan fecha y servicio
3. Llama a `TurnoService.save(turno)`
4. `TurnoService` verifica que el horario no esté ocupado (`turnoRepository.findByServicioIdAndFecha(...)`)
5. Si está libre, llama a `turnoRepository.save(turno)` → INSERT en PostgreSQL
6. `TurnoController` devuelve `200 OK` con el turno guardado (incluyendo el `id` generado)

**¿Por qué hay Interfaces (`interfaces/`)?**

Separa el **qué hace** (interface) del **cómo lo hace** (service). El controller depende de `ITurnoService`, no de `TurnoService` directamente. Esto cumple el principio de inversión de dependencias (la D de SOLID). En la práctica, permite reemplazar la implementación (por ejemplo, para tests o para cambiar la lógica) sin modificar el controller.

**¿Qué son los DTOs (`dto/`)?**

DTO = Data Transfer Object. Son clases simples que representan los datos que llegan del cliente en el cuerpo de una request, cuando esos datos no coinciden exactamente con una entity.

```java
// dto/LoginRequest.java
public class LoginRequest {
    private String username;
    private String password;
}
```

En el login, el frontend manda solo `username` y `password`, no un `UsuarioEntity` completo. El DTO es esa estructura intermedia. Evita exponer la entity directamente en la API (buena práctica de seguridad).

---

## 4. Flujo completo de una petición

```
Usuario completa formulario "Pedir Turno" en el navegador
          │
          │ POST /api/v1/pacientes  (crea/busca el paciente por RUT)
          ▼
    Angular HttpClient
          │
          │ (en dev: proxy.conf.json redirige a localhost:6789)
          │ (en prod: llama directo a Render backend)
          ▼
    Spring Boot — PacienteController.save()
          │ PacienteService verifica si ya existe el RUT
          │ Si existe: actualiza datos y retorna el existente
          │ Si no: crea nuevo paciente en BD
          ▼
    Angular recibe { id: 5, nombreCompleto: "...", ... }
          │
          │ POST /api/v1/turnos  (crea el turno con paciente.id)
          ▼
    Spring Boot — TurnoController.save()
          │ TurnoService verifica disponibilidad del horario
          │ Si disponible: guarda turno
          │ Si ocupado: lanza IllegalArgumentException → 400 Bad Request
          ▼
    Angular recibe 200 OK → muestra SweetAlert "¡Turno solicitado!"
```

---

## 5. Base de datos y relaciones

```
usuario ──────────────── paciente
  │ id (PK)                │ id (PK)
  │ username               │ nombre_completo
  │ password               │ rut
  │ rol (ADMIN/PROF/PAC)   │ telefono
  │ imagen_base64          │ email
                           │ usuario_id (FK → usuario.id)
                           │ fecha_creacion


servicio ──────────────── profesional
  │ id (PK)                │ id (PK)
  │ nombre                 │ nombre_completo
  │ imagen_base64          │ imagen_base64
                           │ servicio_id (FK → servicio.id)


turno
  │ id (PK)
  │ fecha (DATE)
  │ horario (VARCHAR)
  │ estado (PENDIENTE/CONFIRMADO/CANCELADO/COMPLETADO)
  │ mensaje_adicional
  │ paciente_id (FK → paciente.id)
  │ servicio_id (FK → servicio.id)
  └ fecha_creacion
```

**Relaciones:**
- Un `paciente` puede tener muchos `turnos` (1:N)
- Un `servicio` puede tener muchos `turnos` (1:N)
- Un `servicio` puede tener muchos `profesionales` (1:N)
- Un `usuario` puede estar vinculado a un `paciente` (1:1 opcional)

---

## 6. Decisiones técnicas relevantes

### Imágenes en base de datos
Las imágenes se guardan como texto (`imagenBase64` — columna `TEXT` en PostgreSQL). Dos formatos son válidos:
- URL externa: `https://picsum.photos/seed/...` (las del seed)
- Base64 del archivo: `iVBORw0KGgo...` (las subidas por usuario desde formularios)

El frontend detecta automáticamente cuál es cuál:
```typescript
img.startsWith('http') ? img : 'data:image/jpeg;base64,' + img
```

### Seed de datos
El `DataInitializer` (`config/`) solo corre si `APP_FORCE_TEST_DATA=true`. Por defecto es `false`, así que **no borra datos al reiniciar**. Para poblar la BD desde cero existe el endpoint:
```
POST /api/v1/seed   → crea todos los datos de prueba
DELETE /api/v1/seed → borra todo
```

### Autenticación
No hay JWT ni sesiones en el servidor. El usuario logueado se guarda en `localStorage` del navegador:
```typescript
localStorage.setItem('clinica_current_user', JSON.stringify(user));
```
Es suficiente para un proyecto académico. En producción real se usaría JWT o Spring Security con sesiones.

### Prevención de turnos duplicados
`TurnoService.save()` consulta los horarios ocupados **antes de guardar**:
```java
List<String> disponibles = obtenerHorariosDisponibles(servicioId, fecha);
if (!disponibles.contains(turno.getHorario())) {
    throw new IllegalArgumentException("El horario no está disponible");
}
```
El frontend además muestra los slots ya tomados como `disabled` en el selector.

### Pacientes duplicados
`PacienteService.save()` busca primero por RUT. Si ya existe ese RUT, actualiza los datos y retorna el existente en lugar de crear un duplicado:
```java
Optional<PacienteEntity> existente = pacienteRepository.findByRut(paciente.getRut());
if (existente.isPresent()) { /* actualiza y retorna */ }
```
