# Explicación técnica del proyecto FullStack

---

## 1. Qué es una rama y cómo se hizo el merge

### El problema a resolver
El proyecto tenía dos ramas separadas trabajadas por personas distintas:
- `Frontend` → la app Angular (Subaru)
- `Backend` → la API Spring Boot (Ignacio)

Ninguna rama sabía de la otra. Había que unirlas en un mismo lugar para que pudieran funcionar juntas.

### Qué es un merge
Un **merge** es el proceso de fusionar el historial de una rama dentro de otra. Git compara los archivos de ambas ramas y los combina. Si los archivos son distintos (uno tiene `Frontend/` y el otro tiene `Backend/`), Git simplemente los une sin conflicto.

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

Ignacio las creó en Java con `@Entity` (Spring Boot). Nosotros las recreamos en TypeScript como `interface` (Angular). Son el mismo concepto, expresado en cada lenguaje.

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
