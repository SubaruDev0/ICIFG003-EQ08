# Explicación técnica del proyecto FullStack

---

## 1. Qué es una rama y cómo se hizo el merge

### El problema a resolver
El proyecto tenía dos ramas separadas trabajadas por personas distintas:
- `Frontend` → la app Angular (Subaru)
- `Backend` → la API Spring Boot (Ignacio)

Ninguna rama sabía de la otra. Había que unirlas en un mismo lugar para que pudieran funcionar juntas.

### Cómo se hizo paso a paso

**Paso 1 — Crear la rama FullStack desde Frontend:**
```bash
git checkout -b FullStack
# Crea una rama nueva llamada FullStack copiando exactamente el estado actual de Frontend
```

**Paso 2 — Traer los cambios del remoto (GitHub):**
```bash
git fetch origin
# Descarga el estado actualizado de todas las ramas remotas sin aplicar nada todavía
```

**Paso 3 — Fusionar el Backend dentro de FullStack:**
```bash
git merge origin/Backend --no-edit
# Toma todos los commits de la rama Backend y los incorpora a FullStack
# --no-edit: usa el mensaje de merge automático sin abrir el editor
```

**Resultado:** La rama FullStack quedó con la carpeta `Frontend/` (Angular) y la carpeta `Backend/` (Spring Boot) conviviendo juntas.

```
FullStack/
├── Frontend/    ← vino de la rama Frontend
├── Backend/     ← vino de la rama Backend
├── .env         ← nuevo (no va a git)
└── INTEGRACION.md
```

---

## 2. Qué son las entidades y por qué las creamos

### Qué es una entidad
Una **entidad** representa un objeto del mundo real que el sistema necesita guardar en la base de datos. Cada entidad se convierte en una tabla en PostgreSQL.

Ignacio las creó en Java con `@Entity` (Spring Boot). Subaru las recreo en TypeScript como `interface` (Angular). Son el mismo concepto, expresado en cada lenguaje.

### Por qué existen las dos versiones

| Versión | Lenguaje | Dónde vive | Para qué sirve |
|---------|----------|------------|----------------|
| `@Entity` Java | Spring Boot | Backend | Define la tabla en la BD y valida los datos al guardar |
| `interface` TypeScript | Angular | Frontend | Da forma al dato que llega/sale de la API, activa el autocompletado |

Sin la `interface` en Angular, TypeScript no sabe qué campos tiene el objeto que devuelve la API y podrías cometer errores de tipeo sin que el compilador te avise.

---

### Las 5 entidades del proyecto

#### Usuario
Representa a alguien con acceso al sistema (login).

```typescript
interface Usuario {
  id?: number;       // lo genera la BD automáticamente
  username: string;  // nombre de usuario para iniciar sesión
  password?: string; // contraseña (opcional al recibir por seguridad)
  rol: string;       // ej: "ADMIN", "PACIENTE"
}
```

**Por qué existe:** para controlar quién puede entrar al sistema y con qué permisos.

---

#### Paciente
La persona que solicita un turno.

```typescript
interface Paciente {
  id?: number;
  nombreCompleto: string;
  rut: string;           // identificación chilena
  telefono: string;
  email: string;
  usuario?: Usuario | null; // puede tener cuenta o ser invitado
}
```

**Por qué existe:** cuando alguien pide un turno, hay que registrar sus datos de contacto para que la clínica pueda confirmar o contactarlo. El campo `usuario` es opcional porque un paciente puede pedir turno sin estar registrado en el sistema.

---

#### Servicio
Un tratamiento o especialidad que ofrece la clínica.

```typescript
interface Servicio {
  id?: number;
  nombre: string;  // ej: "Ortodoncia", "Implantes dentales"
}
```

**Por qué existe:** para que los turnos queden registrados con el tipo de atención solicitada, y para que la clínica pueda filtrar turnos por especialidad.

---

#### Profesional
Un dentista o especialista que trabaja en la clínica.

```typescript
interface Profesional {
  id?: number;
  nombreCompleto: string;
  imagenBase64: string;  // foto codificada como texto (se guarda en la BD)
  servicio: Servicio;    // especialidad que ejerce
}
```

**Por qué existe:** para mostrar el equipo dinámicamente desde la BD en vez de tener los nombres hardcodeados en el HTML. Así Ignacio puede agregar/editar profesionales desde el backend sin tocar código.

> **Qué es Base64:** es una forma de convertir una imagen (o cualquier archivo binario) en texto. Se hace para poder enviarlo como JSON, ya que JSON solo maneja texto. La imagen se convierte a una cadena larga de caracteres y se guarda en la BD como texto.

---

#### Turno
La cita médica agendada.

```typescript
interface Turno {
  id?: number;
  fecha: string;            // formato "YYYY-MM-DD", ej: "2026-05-15"
  horario: string;          // ej: "mañana", "tarde"
  mensajeAdicional: string; // observaciones del paciente
  paciente: Paciente;       // quién agenda
  servicio: Servicio;       // qué tratamiento pide
}
```

**Por qué existe:** es el registro central del negocio. Une al paciente con el servicio en una fecha. Sin esta entidad no hay sistema de turnos.

---

### Relación entre entidades (diagrama)

```
Usuario (1) ─────── (0..1) Paciente
                              │
                              │ agenda
                              ▼
Servicio (1) ◄──────────── Turno
    │
    │ especialidad de
    ▼
Profesional
```

Un `Turno` siempre necesita un `Paciente` y un `Servicio`.
Un `Profesional` siempre pertenece a un `Servicio`.
Un `Paciente` puede o no tener un `Usuario`.

---

## 3. Por qué usamos DB_URL en vez de poner la URL directamente

### El problema de hardcodear credenciales

Imagina que en `application.properties` escribes directamente:
```properties
spring.datasource.url=jdbc:postgresql://ep-mihost.neon.tech/db?user=admin&password=abc123
```

Eso se sube a GitHub. Cualquier persona que vea el repositorio (público o no, ya que los históricos de git son permanentes) tiene acceso a tu base de datos. Es un problema grave de seguridad.

### La solución: variables de entorno

Una **variable de entorno** es una variable que existe en el sistema operativo (o en la plataforma de deploy), no en el código. El código solo dice "lee esta variable", pero el valor real solo existe en la máquina donde corre.

```properties
# En el código (se sube a GitHub): ✅ seguro
spring.datasource.url=${DB_URL}

# En el .env (NO se sube a GitHub): 🔒 privado
DB_URL=jdbc:postgresql://host.neon.tech/db?user=admin&password=abc123
```

Spring Boot lee `${DB_URL}` y va a buscar esa variable en el entorno del sistema operativo. Si no la encuentra, falla con un error claro.

### Por qué la URL de Neon ya incluye usuario y contraseña

Neon usa el formato JDBC estándar de PostgreSQL donde las credenciales van dentro de la URL como parámetros:
```
jdbc:postgresql://HOST/DATABASE?user=USUARIO&password=CONTRASEÑA&sslmode=require
```

Por eso no necesitamos `DB_USERNAME` y `DB_PASSWORD` como variables separadas: ya están dentro de `DB_URL`.

---

## 4. Por qué es necesario el PORT

### Qué es un puerto
Un puerto es un número que identifica a qué proceso dentro de un servidor se dirige una conexión de red. Es como el número de departamento en un edificio: la IP es el edificio, el puerto es el departamento.

Ignacio configuró el Backend para correr en el puerto `6789`:
```properties
server.port=6789
```

### Por qué necesita ser variable de entorno en Render

Render (y la mayoría de plataformas cloud) **asigna el puerto automáticamente** y lo pone en una variable de entorno llamada `PORT`. Tú no eliges el puerto; Render lo decide y te lo dice.

Si hardcodeas `server.port=6789` y Render asigna el puerto `10000`, tu backend arranca en `6789` pero Render espera tráfico en `10000`. El resultado: Render no puede comunicarse con tu app y la marca como caída.

Con la variable:
```properties
server.port=${PORT:6789}
```

Esto dice: "usa el valor de la variable PORT si existe, si no, usa 6789 por defecto". En local usa `6789`, en Render usa el que Render asigne.

---

## 5. Cómo desplegar en Render

### Backend (Spring Boot)

1. Ir a [render.com](https://render.com) → **New Web Service**
2. Conectar el repositorio de GitHub
3. Configurar:
   - **Branch:** `FullStack`
   - **Root Directory:** `Backend`
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/back-0.0.1-SNAPSHOT.jar`
4. En **Environment Variables** agregar:
   ```
   DB_URL = (pegar la JDBC URL de Neon)
   ```
   > Render asigna `PORT` automáticamente, no hay que agregarlo.

### Frontend (Angular)

1. **New Static Site** en Render
2. Conectar el mismo repositorio
3. Configurar:
   - **Branch:** `FullStack`
   - **Root Directory:** `Frontend`
   - **Build Command:** `npm install && npm run build`
   - **Publish Directory:** `dist/clinica-dental/browser`
4. Una vez desplegado el Backend, copiar su URL (ej: `https://mi-backend.onrender.com`)
5. En el Frontend, editar [environment.prod.ts](Frontend/src/environments/environment.prod.ts):
   ```typescript
   export const environment = {
     production: true,
     apiUrl: 'https://mi-backend.onrender.com'  // ← URL real de Render
   };
   ```
6. Hacer commit y push para que Render rebuilde.

### Flujo completo en producción

```
Usuario en el navegador
        │
        ▼
  Render (Frontend)          ← Angular compilado, archivos estáticos
  https://mi-front.onrender.com
        │  peticiones HTTP
        ▼
  Render (Backend)           ← Spring Boot corriendo
  https://mi-backend.onrender.com
        │  queries SQL
        ▼
  Neon (Base de datos)       ← PostgreSQL en la nube
  ep-xxx.neon.tech
```

---

## Resumen

| Concepto | Por qué existe |
|----------|---------------|
| Rama FullStack | Unir el trabajo del Frontend y Backend en un solo lugar |
| Entidades Java | Definen las tablas en la base de datos |
| Interfaces TypeScript | Dan tipo a los datos en Angular (mismo concepto, otro lenguaje) |
| DB_URL como variable | Para no subir credenciales a GitHub |
| PORT como variable | Render asigna el puerto dinámicamente; si hardcodeas falla el deploy |
| .env | Archivo local con los valores reales, ignorado por git |
| .env.example | Plantilla que sí va a git, para que otros sepan qué variables definir |

---

## 6. Comparación entre entidades Backend (Java) y Frontend (TypeScript)

El flujo completo de un dato es:

```
[Angular Form] → [TypeScript interface] → [JSON] → [HTTP] → [Java object] → [PostgreSQL tabla]
```

Cada entidad existe en ambos lados. El Backend es la fuente de verdad; el Frontend la espeja.

---

### Usuario

| Aspecto | Backend (Java) | Frontend (TypeScript) |
|---------|---------------|----------------------|
| Archivo | `UsuarioEntity.java` | `usuario.model.ts` |
| Tabla en BD | `usuario` | — (no toca la BD) |

```java
// BACKEND — crea la tabla y valida al guardar
@Entity
@Table(name = "usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // Long: número grande de 64 bits

    private String username;
    private String password; // requerido, sin null
    private String rol;
}
```

```typescript
// FRONTEND — solo describe la forma del dato
export interface Usuario {
  id?: number;        // number: engloba int y long de Java
  username: string;
  password?: string;  // opcional: al LEER un usuario, el back no devuelve la contraseña
  rol: string;
}
```

**Diferencia clave:** `password` es obligatorio en Java (el backend lo necesita para guardar) pero opcional en TypeScript (el backend nunca devuelve la contraseña al frontend por seguridad, entonces al leerlo el campo no existe).

---

### Paciente

| Aspecto | Backend (Java) | Frontend (TypeScript) |
|---------|---------------|----------------------|
| Relación con Usuario | `@OneToOne` (una fila en BD referencia otra fila) | `usuario?: Usuario \| null` |
| Columna de unión | `usuario_id` (clave foránea en la tabla `paciente`) | — (transparente para Angular) |

```java
// BACKEND
@Entity
@Table(name = "paciente")
public class PacienteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String rut;
    private String telefono;
    private String email;

    @OneToOne                          // ← relación: 1 paciente = 1 usuario
    @JoinColumn(
        name = "usuario_id",           // ← columna que se crea en la tabla paciente
        referencedColumnName = "id",
        nullable = true                // ← puede ser null (paciente sin cuenta)
    )
    private UsuarioEntity usuario;
}
```

```typescript
// FRONTEND
export interface Paciente {
  id?: number;
  nombreCompleto: string;
  rut: string;
  telefono: string;
  email: string;
  usuario?: Usuario | null; // null = paciente sin cuenta registrada
}
```

**Qué es `@OneToOne`:** significa que en la tabla `paciente` de PostgreSQL existe una columna `usuario_id`. Cuando Hibernate guarda un `PacienteEntity`, en esa columna pone el `id` del usuario relacionado. En el frontend esto es invisible: Angular solo ve el objeto `usuario` anidado dentro de `paciente`.

---

### Servicio

La más simple. Un tratamiento tiene solo nombre.

```java
// BACKEND
@Entity
@Table(name = "servicio")
public class ServicioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;  // "Ortodoncia", "Implantes", etc.
}
```

```typescript
// FRONTEND
export interface Servicio {
  id?: number;
  nombre: string;
}
```

**Sin diferencias** conceptuales. La única diferencia técnica es `Long` vs `number`.

---

### Profesional

Aquí está la decisión del Base64.

```java
// BACKEND
@Entity
@Table(name = "profesional")
public class ProfesionalEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;

    @Column(columnDefinition = "TEXT")  // ← TEXT porque base64 es muy largo
    private String imagenBase64;        // ← nombre que define el contrato con el frontend

    @ManyToOne                          // ← muchos profesionales pueden tener el mismo servicio
    @JoinColumn(name = "servicio_id")   // ← columna FK en la tabla profesional
    private ServicioEntity servicio;
}
```

```typescript
// FRONTEND
export interface Profesional {
  id?: number;
  nombreCompleto: string;
  imagenBase64: string;   // ← mismo nombre que el campo Java (contrato JSON)
  servicio: Servicio;     // ← objeto anidado, no el id, el objeto completo
}
```

**Qué es `@ManyToOne`:** muchos profesionales pueden pertenecer al mismo servicio (ej: varios dentistas hacen "Ortodoncia"). En la tabla `profesional` hay una columna `servicio_id` que apunta a qué fila de la tabla `servicio` corresponde.

**Cómo se usa la imagen en el HTML:**
```html
<!-- Angular convierte el string base64 en una imagen real -->
<img [src]="'data:image/jpeg;base64,' + prof.imagenBase64">
```

---

### Turno

La entidad más compleja: tiene relaciones con `Paciente` y `Servicio`.

```java
// BACKEND
@Entity
@Table(name = "turno")
public class TurnoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)    // ← guarda solo la fecha, sin hora, como DATE en PostgreSQL
    private Date fecha;             // ← tipo Date de Java

    private String horario;

    @Column(columnDefinition = "TEXT")
    private String mensajeAdicional;

    @ManyToOne                      // ← muchos turnos pueden tener el mismo paciente
    @JoinColumn(name = "paciente_id")
    private PacienteEntity paciente;

    @ManyToOne                      // ← muchos turnos pueden ser del mismo servicio
    @JoinColumn(name = "servicio_id")
    private ServicioEntity servicio;
}
```

```typescript
// FRONTEND
export interface Turno {
  id?: number;
  fecha: string;           // ← string "YYYY-MM-DD", NO Date de JS
  horario: string;
  mensajeAdicional: string;
  paciente: Paciente;      // ← objeto completo, no solo el id
  servicio: Servicio;      // ← objeto completo, no solo el id
}
```

**Diferencia clave en `fecha`:** Java usa `Date`, TypeScript usa `string`. Cuando Angular envía `"2026-05-15"` como string en el JSON, Spring Boot lo convierte automáticamente a un objeto `Date` de Java gracias a `@Temporal`. No hay que hacer nada manual.

**Dos `@ManyToOne`:** la tabla `turno` en PostgreSQL tiene dos columnas FK: `paciente_id` y `servicio_id`. Cuando el backend devuelve un turno, Hibernate hace automáticamente los JOINs y devuelve los objetos completos anidados — no los IDs solos.

---

### Flujo completo: el usuario pide un turno

```
1. Angular (pedir-turno.component.ts)
   └─ construye objeto TypeScript:
      {
        fecha: "2026-05-20",
        horario: "mañana",
        mensajeAdicional: "Me duele una muela",
        paciente: { nombreCompleto: "Juan", rut: "12.345.678-9", ... },
        servicio: { id: 3, nombre: "Endodoncia" }
      }

2. TurnoService (turno.service.ts)
   └─ this.http.post("/api/v1/turnos", turno)
      └─ HttpClient serializa el objeto a JSON automáticamente

3. JSON viaja por HTTP al Backend
   └─ POST http://localhost:6789/api/v1/turnos
      Content-Type: application/json
      Body: { "fecha": "2026-05-20", "horario": "mañana", ... }

4. Spring Boot (TurnoController.java)
   └─ @PostMapping recibe el JSON
      └─ Jackson (librería de Spring) deserializa el JSON
         a un objeto TurnoEntity de Java automáticamente

5. Spring Boot (TurnoService.java)
   └─ turnoRepository.save(turno)
      └─ Hibernate genera el SQL:
         INSERT INTO turno (fecha, horario, mensaje_adicional, paciente_id, servicio_id)
         VALUES ('2026-05-20', 'mañana', 'Me duele una muela', 42, 3)

6. PostgreSQL (Neon)
   └─ guarda la fila en la tabla turno
      └─ devuelve la fila con el id generado (ej: id=87)

7. La respuesta sube de vuelta:
   PostgreSQL → Hibernate → Spring Boot → JSON → HTTP → Angular
   Angular muestra: "Turno solicitado correctamente"
```
