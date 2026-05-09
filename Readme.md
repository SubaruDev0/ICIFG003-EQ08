# ICIFG003-EQ08 - Guía de Levantamiento Local

Link producción Frontend: https://icifg003-eq08.onrender.com  
Link producción Backend: https://icifg003-eq08-back.onrender.com

**Las variables de entorno están expuestas intencionalmente** para facilitar la revisión. (no hacer esto en casa xd)

## 1. Clonar el repositorio
```bash
git clone git@github.com:SubaruDev0/ICIFG003-EQ08.git
cd ICIFG003-EQ08
```

## 2. Levantar BACKEND en local
Requisitos:
- Java 17+
- Maven 3.8+

Comandos:
```bash
cd Backend
mvn spring-boot:run
```

Backend local:
- `http://localhost:6789`

Healthcheck:
```bash
curl http://localhost:6789/api/v1/health
```

Si el puerto está ocupado:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=6790
```

## 3. Levantar FRONTEND en local
Requisitos:
- Node 18+

Comandos:
```bash
cd Frontend
npm install
npm run start -- --proxy-config proxy.conf.json
```

Frontend local:
- `http://localhost:4200`

Nota corta y humana sobre `--proxy-config`: ese parámetro le dice a Angular que las rutas `/api` las mande al backend, así evitamos problemas de CORS en desarrollo.

## 4. Crear y probar base de datos local (PostgreSQL + `psql`)

Requisitos:
- PostgreSQL instalado localmente (incluye `psql`)

Crear la base de datos local (si no existe):

```bash
createdb -U postgres clinica_dental
```

Alternativa desde `psql`:

```bash
psql -U postgres -d postgres
CREATE DATABASE clinica_dental;
\q
```

Conectarte con `psql` a la base local (nombre: `clinica_dental`):
```bash
psql -U postgres -d clinica_dental
```

Ver tablas:
```sql
\dt
```

Ver datos:
```sql
SELECT id, username, rol FROM usuario ORDER BY id;
SELECT id, nombre FROM servicio ORDER BY id;
SELECT id, nombre_completo, servicio_id FROM profesional ORDER BY id;
SELECT id, nombre_completo, rut, usuario_id FROM paciente ORDER BY id;
SELECT id, fecha, horario, paciente_id, servicio_id, estado FROM turno ORDER BY id;

-- Extras útiles
SELECT COUNT(*) AS total_usuarios FROM usuario;
SELECT COUNT(*) AS total_turnos FROM turno;
```

## 5. Poblar la base de datos (seed)

El seed **no corre automáticamente** al iniciar el backend. Se ejecuta manualmente con un comando curl, una sola vez.

Con el backend corriendo:
```bash
# Poblar todo desde cero
curl -X POST http://localhost:6789/api/v1/seed

# Resetear (borrar todo) y volver a poblar
curl -X DELETE http://localhost:6789/api/v1/seed
curl -X POST http://localhost:6789/api/v1/seed
```

Si la base ya tiene datos, el POST avisa y no hace nada (hay que hacer DELETE primero).

Usuarios creados:
- `admin` / `admin123` / `ADMIN`
- `dra.martinez` / `prof123` / `PROFESIONAL`
- `dr.gomez` / `prof123` / `PROFESIONAL`
- `dra.lopez` / `prof123` / `PROFESIONAL`
- `paciente1` / `pac123` / `PACIENTE`
- `paciente2` / `pac123` / `PACIENTE`

Servicios creados:
- Odontologia General
- Ortodoncia
- Implantes Dentales
- Endodoncia
- Blanqueamiento

Profesionales creados (vinculados a su servicio):
- Dra. Sofia Martinez -> Ortodoncia
- Dr. Carlos Gomez -> Implantes Dentales
- Dra. Ana Lopez -> Endodoncia
- Admin Clinica -> Odontologia General

Pacientes creados (vinculados a su usuario):
- Juan Perez (RUT 12345678-9) -> usuario `paciente1`
- Maria Garcia (RUT 98765432-1) -> usuario `paciente2`

Turnos creados (5 turnos, confirmados y pendientes):
- Juan Perez: Ortodoncia, Ortodoncia, Blanqueamiento
- Maria Garcia: Odontologia General, Implantes Dentales

## 6. Relaciones de datos (resumen)
- `usuario` -> define identidad y rol (`PROFESIONAL` o `PACIENTE`).
- `paciente` -> puede vincularse a `usuario` (`usuario_id`).
- `servicio` -> catálogo de atenciones dentales.
- `profesional` -> asociado a un `servicio` por `servicio_id`.
- `turno` -> une `paciente` + `servicio` + `fecha` + `horario`.

## 7. Pruebas rápidas por API
Con backend local corriendo:

```bash
# Servicios
curl http://localhost:6789/api/v1/servicios

# Profesionales
curl http://localhost:6789/api/v1/profesionales

# Login (ojo: solo una barra invertida por línea)
curl -X POST http://localhost:6789/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Horarios disponibles
curl "http://localhost:6789/api/v1/turnos/disponibles?servicioId=1&fecha=2026-05-10"
```

## 8. Qué quedó ajustado en frontend
- Header: muestra foto, nombre y rol del usuario logueado.
- Inicio: carrusel de servicios ahora sale desde la base (sin hardcode de servicios).
- Equipo: especialistas cargados desde backend.
- Formularios: piden solo datos necesarios para persistir en BD.

## Modificaciones
Estas notas complementan la guía original sin reemplazarla.

### A) Orden recomendado para evaluación local (sin Docker)
1. Clonar repositorio.
2. Verificar herramientas:
   ```bash
   java -version
   mvn -v
   node -v
   npm -v
   ```
3. Crear/verificar PostgreSQL local (`clinica_dental`).
4. Levantar backend.
5. Poblar seed (si corresponde).
6. Levantar frontend.
7. Ejecutar pruebas rápidas por API.

### B) Clonado por HTTPS (alternativa a SSH)
Si no tienes llave SSH configurada:
```bash
git clone https://github.com/SubaruDev0/ICIFG003-EQ08.git
cd ICIFG003-EQ08
```

### C) Backend: validación de compilación y tests
Además de `mvn spring-boot:run`, para validar build completo:
```bash
cd Backend
mvn clean install
```

### D) Nota de consistencia
- Este `Readme.md` raíz es la guía principal de ejecución.
- `Frontend/README.md` corresponde al template base de Angular CLI.

### E) Ejecución en Windows (VM) - PowerShell
Validar herramientas:
```powershell
java -version
mvn -v
where.exe mvn
node -v
npm -v
```

Si `mvn` falla por PATH, usar Maven Wrapper del proyecto:
```powershell
cd Backend
.\mvnw.cmd -v
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

Validar PostgreSQL local antes de iniciar backend:
```powershell
psql -h localhost -U postgres -d clinica_dental -c "\conninfo"
```

Si falta crear la base:
```powershell
createdb -h localhost -U postgres clinica_dental
```

Frontend en Windows:
```powershell
cd Frontend
npm install
npm run start -- --proxy-config proxy.conf.json
```

### F) Instalación rápida por terminal (Windows VM)
Ejecutar PowerShell **como administrador** y correr:

```powershell
winget install --id Git.Git -e --source winget
winget install --id EclipseAdoptium.Temurin.17.JDK -e --source winget
winget install --id Apache.Maven -e --source winget
winget install --id OpenJS.NodeJS.LTS -e --source winget
winget install --id PostgreSQL.PostgreSQL -e --source winget
```

Luego cerrar y abrir PowerShell, y verificar:

```powershell
git --version
java -version
mvn -v
node -v
npm -v
psql --version
```

Si `mvn` no aparece en PATH, usar wrapper del proyecto:

```powershell
cd Backend
.\mvnw.cmd -v
```
