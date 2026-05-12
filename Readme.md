# ICIFG003-EQ08 - Guía de Ejecución Local (Solo Windows)

Esta guía está pensada para ejecutarse en una VM Windows del profesor.

## 1. Requisitos en la VM

Ya preinstalado en su contexto:
- Java
- Spring Tool Suite (STS)
- Git
- PostgreSQL (`psql`)

Necesitas instalar:
- Node.js (incluye npm)
- Angular CLI (`ng`)

## 2. Instalación en Windows

Abrir PowerShell **como administrador** y ejecutar:

```powershell
winget install --id OpenJS.NodeJS.LTS -e --source winget
```

Luego instalar Angular CLI:

```powershell
npm install -g @angular/cli
```

Si PowerShell bloquea scripts/comandos, ejecutar una vez:

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

Después cierra y vuelve a abrir PowerShell.

## 3. Verificar herramientas

```powershell
java -version
git --version
psql --version
node -v
npm -v
ng version
```

## 4. Clonar el proyecto

Con SSH:

```powershell
git clone git@github.com:SubaruDev0/ICIFG003-EQ08.git
cd ICIFG003-EQ08
```

Con HTTPS (si no usas llave SSH):

```powershell
git clone https://github.com/SubaruDev0/ICIFG003-EQ08.git
cd ICIFG003-EQ08
```

## 5. Crear base de datos PostgreSQL

La app usa por defecto:
- DB: `demo01`
- Usuario: `postgres`
- Password: `1234`
- Puerto DB: `5432`

Entrar a PostgreSQL:

```powershell
psql -h localhost -U postgres -d postgres
```

Dentro de `psql`:

```sql
CREATE DATABASE demo01;
\q
```

Verificar conexión:

```powershell
psql -h localhost -U postgres -d demo01 -c "\conninfo"
```

## 6. Ejecutar backend (Windows)

Este proyecto usa Maven Wrapper, así que **no necesitas instalar Maven global**.

```powershell
cd Backend
.\mvnw.cmd spring-boot:run
```

Backend disponible en:
- `http://localhost:6789`

Health checks:

```powershell
curl http://localhost:6789/
curl http://localhost:6789/health
```

## 7. Ejecutar frontend (Windows)

En otra terminal PowerShell:

```powershell
cd Frontend
npm install
npm run start -- --proxy-config proxy.conf.json
```

Frontend disponible en:
- `http://localhost:4200`

## 8. Hidratar base de datos (solo servicios)

Con backend corriendo:

```powershell
curl -X POST http://localhost:6789/api/v1/servicios -H "Content-Type: application/json" -d "{\"nombre\":\"Odontologia General\",\"imagenBase64\":\"https://picsum.photos/seed/odontologia/600/400\"}"
curl -X POST http://localhost:6789/api/v1/servicios -H "Content-Type: application/json" -d "{\"nombre\":\"Ortodoncia\",\"imagenBase64\":\"https://picsum.photos/seed/ortodoncia/600/400\"}"
curl -X POST http://localhost:6789/api/v1/servicios -H "Content-Type: application/json" -d "{\"nombre\":\"Implantes Dentales\",\"imagenBase64\":\"https://picsum.photos/seed/implantes/600/400\"}"
curl -X POST http://localhost:6789/api/v1/servicios -H "Content-Type: application/json" -d "{\"nombre\":\"Endodoncia\",\"imagenBase64\":\"https://picsum.photos/seed/endodoncia/600/400\"}"
curl -X POST http://localhost:6789/api/v1/servicios -H "Content-Type: application/json" -d "{\"nombre\":\"Blanqueamiento Dental\",\"imagenBase64\":\"https://picsum.photos/seed/blanqueamiento/600/400\"}"
```

Verificar servicios:

```powershell
curl http://localhost:6789/api/v1/servicios
```

## 9. Hidratar base de datos (resto de datos)

```powershell
curl -X DELETE http://localhost:6789/api/v1/seed
curl -X POST http://localhost:6789/api/v1/seed
```

Importante:
- `POST /api/v1/seed` requiere base vacía.
- `POST /api/v1/seed` también crea servicios.

## 10. Usuarios de prueba (seed)

- `admin` / `admin123` / `ADMIN`
- `dra.martinez` / `prof123` / `PROFESIONAL`
- `dr.gomez` / `prof123` / `PROFESIONAL`
- `dra.lopez` / `prof123` / `PROFESIONAL`
- `paciente1` / `pac123` / `PACIENTE`
- `paciente2` / `pac123` / `PACIENTE`
