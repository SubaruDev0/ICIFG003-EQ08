# Frontend — Clínica Dental (Angular 17)

Migración completa del frontend original (HTML + CSS + JS puro) a **Angular 17** con componentes standalone, routing y TypeScript.

---

## Versiones

| Herramienta | Versión usada |
|---|---|
| Node.js | 18.19.x |
| npm | 9.2.x |
| Angular CLI | 17.3.x |
| Angular (framework) | 17.x |
| TypeScript | 5.x |

> Con Node 20+ se puede instalar Angular CLI 18/19. La app es compatible.

---

## Instalación y ejecución

```bash
cd Frontend
npm install        # instala todas las dependencias
ng serve           # levanta en http://localhost:4200
```

Para compilar a producción:

```bash
ng build
# Genera los archivos listos para subir a un servidor en dist/clinica-dental/
```

---

## Rutas de la aplicación

| Ruta | Página |
|---|---|
| `/login` | Pantalla de acceso con animación |
| `/inicio` | Página principal: banner, carrusel, testimonios |
| `/servicios` | Catálogo de 9 especialidades |
| `/equipo` | Especialistas, equipo de apoyo, certificaciones |
| `/contacto` | Mapa, sedes secundarias, horarios y contacto |
| `/pedir-turno` | Formulario para solicitar cita |

La ruta raíz `/` redirige automáticamente a `/login`.

**Credenciales de prueba:** usuario `Blas`, contraseña `123`.
También hay un botón "Acceder como invitado" que salta la validación.

---

## Qué pasó con la carpeta `Frontend/` original

La carpeta `Frontend/` (HTML + CSS + JS) fue **eliminada** en esta rama. Su contenido se migró de la siguiente manera:

| Antes (Frontend/) | Ahora (clinica-dental/src/) |
|---|---|
| `inicio.html` | `app/pages/inicio/inicio.component.html` |
| `servicios.html` | `app/pages/servicios/servicios.component.html` |
| `equipo.html` | `app/pages/equipo/equipo.component.html` |
| `contacto.html` | `app/pages/contacto/contacto.component.html` |
| `login.html` | `app/pages/login/login.component.html` |
| `pedirTurno.html` | `app/pages/pedir-turno/pedir-turno.component.html` |
| `css/header-footer.css` | `styles.css` (variables globales) + `components/header/` + `components/footer/` |
| `css/inicio.css` | `app/pages/inicio/inicio.component.css` |
| `css/servicios.css` | `app/pages/servicios/servicios.component.css` |
| `css/equipo.css` | `app/pages/equipo/equipo.component.css` |
| `css/contacto.css` | `app/pages/contacto/contacto.component.css` |
| `css/login.css` | `app/pages/login/login.component.css` |
| `css/pedirTurno.css` | `app/pages/pedir-turno/pedir-turno.component.css` |
| `js/carrusel.js` | `app/pages/inicio/inicio.component.ts` (métodos `scrollLeft`, `scrollRight`, auto-scroll) |
| `js/login.js` | `app/pages/login/login.component.ts` (animación + validación) |
| `js/contacto.js` | `app/pages/contacto/contacto.component.ts` (array `sedes[]` + `*ngFor`) |
| `js/restricciones.js` | `app/pages/pedir-turno/pedir-turno.component.ts` (método `formatearRut()`) |
| `img/` | `assets/img/` |

En Angular **no hay archivos JS ni CSS sueltos**: la lógica va en el `.ts` del componente y los estilos en el `.css` del componente (con scope local, sin afectar otras páginas).

---

## Descripción de cada archivo del proyecto

### Raíz del proyecto

| Archivo | Qué hace |
|---|---|
| `package.json` | Lista todas las dependencias del proyecto (Angular, TypeScript, etc.) y define los scripts `npm start`, `npm run build`. Es el equivalente del `pom.xml` del backend. |
| `package-lock.json` | Archivo generado automáticamente. Fija las versiones exactas de cada paquete instalado para que todos tengan el mismo entorno. No se edita a mano. |
| `angular.json` | Configuración principal de Angular CLI: rutas de entrada, assets, estilos globales, límites de tamaño de bundle para producción. |
| `tsconfig.json` | Configuración del compilador TypeScript para todo el proyecto. |
| `tsconfig.app.json` | Configuración TypeScript específica para la app (extiende `tsconfig.json`). |
| `tsconfig.spec.json` | Configuración TypeScript para los tests unitarios. |
| `.editorconfig` | Reglas de formato de código (indentación, saltos de línea) para que todos los editores generen código consistente. |
| `.gitignore` | Lista de archivos/carpetas que Git ignora (`node_modules/`, `dist/`, etc.). |
| `FRONTEND.md` | Este archivo: documentación del proyecto. |
| `README.md` | Readme generado automáticamente por Angular CLI (genérico, se puede reemplazar). |

### `src/`

| Archivo | Qué hace |
|---|---|
| `src/index.html` | El único HTML real de toda la app. Contiene el `<app-root>` donde Angular monta todo. También carga Font Awesome desde CDN. Angular inyecta aquí los scripts compilados automáticamente. |
| `src/main.ts` | Punto de entrada de la aplicación. Le dice a Angular qué componente raíz usar (`AppComponent`) y con qué configuración arrancar (`appConfig`). |
| `src/styles.css` | Estilos **globales**: variables CSS (colores, fuentes) y reset. Es el equivalente del antiguo `header-footer.css` en su parte de variables. |
| `src/favicon.ico` | Ícono de la pestaña del navegador. |

### `src/app/` — Lógica principal

| Archivo | Qué hace |
|---|---|
| `app.component.ts` | Componente raíz de Angular. No tiene lógica propia, solo actúa como contenedor. |
| `app.component.html` | Template del componente raíz. Solo contiene `<router-outlet>`, que es el espacio donde Angular renderiza la página activa según la ruta. |
| `app.component.css` | CSS del componente raíz (vacío; los estilos viven en cada componente). |
| `app.routes.ts` | Define todas las rutas: qué URL carga qué componente. Equivalente a la barra de navegación del HTML, pero controlado por código. |
| `app.config.ts` | Configura los providers de Angular (enrutador). Es el arranque de la app. |

### `src/app/components/` — Componentes reutilizables

Cada componente tiene tres archivos: `.ts` (lógica), `.html` (estructura), `.css` (estilos).

| Componente | Qué hace |
|---|---|
| `header/` | Barra de navegación con logo y links. Usa `RouterLink` para navegar sin recargar la página y `RouterLinkActive` para resaltar automáticamente el link de la página activa. |
| `footer/` | Pie de página con copyright y redes sociales. Se reutiliza en todas las páginas excepto login. |

### `src/app/pages/` — Páginas de la app

| Componente | Archivos clave | Lógica importante |
|---|---|---|
| `login/` | `.ts` `.html` `.css` | Animación de entrada con `async/await`. Valida usuario `Blas` / contraseña `123`. Redirige con `Router.navigate()`. |
| `inicio/` | `.ts` `.html` `.css` | Carrusel con `@ViewChild` + `ElementRef` + `setInterval`. Botón arriba con `@HostListener('window:scroll')`. |
| `servicios/` | `.ts` `.html` `.css` | Grid de 9 tarjetas. Botón flotante que navega a `/pedir-turno`. |
| `equipo/` | `.ts` `.html` `.css` | Tres secciones: especialistas, apoyo, certificaciones. |
| `contacto/` | `.ts` `.html` `.css` | Array `sedes[]` renderizado con `*ngFor`. Mapa Google embebido con `<iframe>`. |
| `pedir-turno/` | `.ts` `.html` `.css` | Formulario con `[(ngModel)]`. `formatearRut()` formatea el RUT en tiempo real. `fechaMin` bloquea fechas pasadas. |

### `src/assets/`

| Carpeta | Qué contiene |
|---|---|
| `assets/img/` | Todas las imágenes del sitio: logo, banner, fotos del equipo, imágenes de servicios, fotos de sedes. Son las mismas imágenes del `Frontend/img/` original. |

---

## Conexión con el Backend (Spring Boot + PostgreSQL local)

### Pasos generales

**1. Crear un servicio HTTP en Angular**

```bash
# Genera el servicio desde la raíz del proyecto Angular
ng generate service services/api
```

Esto crea `src/app/services/api.service.ts`. Ahí van todas las llamadas al backend:

```typescript
// src/app/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api'; // URL del backend Spring Boot

  constructor(private http: HttpClient) {}

  // Ejemplo: obtener lista de personas
  getPersonas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/persona`);
  }

  // Ejemplo: crear un turno
  crearTurno(turno: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/turnos`, turno);
  }
}
```

**2. Habilitar HttpClient en `app.config.ts`**

```typescript
// src/app/app.config.ts
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';  // ← agregar
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),  // ← agregar
  ]
};
```

**3. Usar el servicio en un componente**

```typescript
// Ejemplo en pedir-turno.component.ts
import { ApiService } from '../../services/api.service';

export class PedirTurnoComponent {
  constructor(private api: ApiService) {}

  onSubmit(): void {
    const turno = { nombre: this.nombre, email: this.email, /* ... */ };
    this.api.crearTurno(turno).subscribe({
      next: () => alert('Turno solicitado correctamente.'),
      error: (err) => console.error('Error:', err)
    });
  }
}
```

**4. Configurar CORS en Spring Boot**

Para que Angular (puerto 4200) pueda hablar con Spring Boot (puerto 8080), el backend necesita:

```java
// En el controlador o en una clase @Configuration
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/turnos")
public class TurnoController { ... }
```

**5. Configurar la conexión a PostgreSQL local**

En el backend Spring Boot, en `application.properties`:

```properties
# PostgreSQL local
spring.datasource.url=jdbc:postgresql://localhost:5432/clinica_dental
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**6. Para producción: cambiar la URL base del API**

En Angular, la URL del backend se puede definir en el archivo de entornos:

```typescript
// src/environments/environment.ts        ← desarrollo
export const environment = { apiUrl: 'http://localhost:8080/api' };

// src/environments/environment.prod.ts   ← producción
export const environment = { apiUrl: 'https://tu-backend.com/api' };
```

Y en `api.service.ts` usas `environment.apiUrl` en lugar del string hardcodeado.

---

## Decisiones de diseño

- **Mismo diseño visual** que el HTML original: colores, fuentes, layouts, animaciones y componentes idénticos.
- **Componentes standalone** (sin NgModules) — patrón moderno de Angular 17, sin boilerplate innecesario.
- **CSS por componente**: cada página tiene sus estilos con scope local. No hay conflictos entre páginas como podía ocurrir con los CSS globales.
- **Sin librerías externas de UI**: el diseño es 100% CSS propio, igual que el original.
- **Font Awesome** se carga desde CDN en `index.html` (igual que el original).
- **`node_modules/`** y **`dist/`** están en `.gitignore` y no se suben al repositorio.
