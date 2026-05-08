# ICIFG003-EQ08 - Guía de Levantamiento Local

Link producción Frontend: https://icifg003-eq08.onrender.com  
Link producción Backend: https://icifg003-eq08-back.onrender.com

**Las variables de entorno están expuestas intencionalmente** para facilitar la revisión. (no hacer esto en casa xd)

## 1. Clonar el repositorio
```bash
git clone <URL_DEL_REPO>
cd ICIFG003-EQ08
```


## 2. Levantar BACKEND en local
Requisitos:
- Java 17
- Maven 3.9+

Comandos:
```bash
cd Backend
mvn spring-boot:run
```

Backend local queda en:
- `http://localhost:6789`

Healthcheck:
```bash
curl http://localhost:6789/api/v1/health
```

Nota: el backend también está desplegado en `https://icifg003-eq08-back.onrender.com`.

## 3. Levantar FRONTEND en local
Requisitos:
- Node 18+
- Angular CLI 17 (opcional global, también funciona con `npx`)

Comandos:
```bash
cd Frontend
npm install
npm run start -- --proxy-config proxy.conf.json
```

Frontend local queda en:
- `http://localhost:4200`

Importante: en desarrollo, el proxy enruta `/api` al backend configurado en `Frontend/proxy.conf.json`.

## 4. Probar conexión a base de datos Neon con psql
Requisito:
- `psql` instalado

Conexión directa:
```bash
psql "postgresql://neondb_owner:npg_9S2vxOtCweTu@ep-polished-band-acvrt1m0-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require"
```

### 4.1 Ver tablas
```sql
\dt
```

### 4.2 Ver estructura de tabla `usuario`
```sql
\d usuario
```

### 4.3 Ver usuarios existentes (actual)
```sql
SELECT id, username, rol FROM usuario ORDER BY id;
```

Resultado esperado actual:
- `id=1`, `username=admin`, `rol=ADMIN`

### 4.4 Ver otras tablas clave
```sql
SELECT * FROM servicio ORDER BY id;
SELECT * FROM profesional ORDER BY id;
SELECT * FROM paciente ORDER BY id;
SELECT * FROM turno ORDER BY id;
```

## 5. Usuarios iniciales para prueba
El backend crea automáticamente un usuario admin si no existe:
- Username: `admin`
- Password: `admin123`
- Rol: `ADMIN`

Variables que controlan ese seed:
- `APP_ADMIN_USERNAME`
- `APP_ADMIN_PASSWORD`
- `APP_ADMIN_ROLE`

## 6. Comandos útiles de validación rápida
Con backend local corriendo:

```bash
# Listar servicios
curl http://localhost:6789/api/v1/servicios

# Listar profesionales
curl http://localhost:6789/api/v1/profesionales

# Login real
curl -X POST http://localhost:6789/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Horarios disponibles
curl "http://localhost:6789/api/v1/turnos/disponibles?servicioId=1&fecha=2026-05-10"
```

## 7. Notas para evaluación
- Front y back pueden levantarse totalmente local.
- La base de datos está en Neon (cloud), pero es accesible por terminal con `psql`.
- Se incluyeron valores públicos por requerimiento explícito de evaluación académica.
