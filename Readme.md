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

## 4. Probar base de datos Neon desde terminal (`psql`)
Conexión directa:
```bash
psql "postgresql://neondb_owner:npg_9S2vxOtCweTu@ep-polished-band-acvrt1m0-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require"
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
