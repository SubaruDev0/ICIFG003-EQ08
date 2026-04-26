# Frontend — Clínica Dental (Angular)

Migración del frontend original (HTML + CSS + JS) a **Angular 17** con componentes standalone.

---

## Versiones requeridas

| Herramienta | Versión mínima |
|---|---|
| Node.js | 18.x (18.13+) |
| npm | 9.x |
| Angular CLI | 17.x |

> Con Node 20+ se puede instalar Angular CLI 18/19 en lugar de la 17.

---

## Instalación y ejecución

```bash
# 1. Entrar a la carpeta del proyecto
cd clinica-dental

# 2. Instalar dependencias
npm install

# 3. Levantar el servidor de desarrollo
npm start
# o bien:
ng serve

# La app queda disponible en http://localhost:4200
```

Para compilar para producción:

```bash
ng build
# Los archivos quedan en dist/clinica-dental/
```

---

## Rutas de la aplicación

| Ruta | Página |
|---|---|
| `/login` | Pantalla de acceso |
| `/inicio` | Página principal |
| `/servicios` | Catálogo de servicios |
| `/equipo` | Equipo profesional |
| `/contacto` | Mapa, sedes y datos de contacto |
| `/pedir-turno` | Formulario para solicitar cita |

La ruta raíz `/` redirige automáticamente a `/login`.

**Credenciales de prueba:** usuario `Blas`, contraseña `123`. También existe el botón "Acceder como invitado".

---

## Estructura del proyecto

```
clinica-dental/
├── src/
│   ├── index.html              # HTML raíz: carga Font Awesome y app-root
│   ├── styles.css              # Variables CSS globales y reset (colores, fuentes)
│   ├── main.ts                 # Punto de entrada de Angular
│   └── app/
│       ├── app.component.html  # Solo contiene <router-outlet>
│       ├── app.component.ts    # Componente raíz
│       ├── app.routes.ts       # Definición de todas las rutas
│       ├── app.config.ts       # Configuración (provideRouter)
│       │
│       ├── components/         # Componentes reutilizables
│       │   ├── header/         # Barra de navegación con logo y links activos
│       │   └── footer/         # Pie de página con redes sociales
│       │
│       └── pages/              # Una carpeta por página/ruta
│           ├── login/          # Animación de entrada + formulario con validación
│           ├── inicio/         # Banner, carrusel de servicios, testimonios
│           ├── servicios/      # Grid con las 9 especialidades
│           ├── equipo/         # Especialistas, equipo de apoyo, certificaciones
│           ├── contacto/       # Mapa Google, carrusel de sedes, tarjetas de info
│           └── pedir-turno/    # Formulario de cita con formato RUT y fecha mínima
│
└── assets/
    └── img/                    # Imágenes del sitio (logo, servicios, equipo, sedes)
```

Cada página tiene **tres archivos propios**:
- `.ts` — lógica del componente (TypeScript, equivalente al .js original)
- `.html` — plantilla (estructura de la vista)
- `.css` — estilos con scope al componente (no afectan al resto de la app)

---

## Lógica por componente

### `header`
Usa `RouterLink` y `RouterLinkActive` de Angular para marcar el enlace de la página activa con la clase CSS `active` automáticamente.

### `login`
- Animación de entrada (expansor azul → logo 3D → formulario) implementada con `async/await` y `setTimeout` en el ciclo de vida `ngOnInit`.
- Validación de credenciales en el método `validarCredenciales()`.
- Redirección con `Router.navigate()`.

### `inicio`
- Carrusel de servicios con scroll horizontal controlado mediante `@ViewChild` y `ElementRef`.
- Auto-scroll cada 3 segundos, pausado al hacer hover.
- Botón "Volver Arriba" con `@HostListener('window:scroll')`.

### `servicios`
- Grid de tarjetas de servicios.
- Botón flotante "Pedir Turno" que navega a `/pedir-turno`.

### `equipo`
- Sección de especialistas, equipo de apoyo y certificaciones.
- Botón flotante "Pedir Turno".

### `contacto`
- El array `sedes[]` reemplaza el objeto `branchesData` del JS original.
- Las tarjetas de sedes se renderizan con `*ngFor`.
- Mapa de Google embebido con `<iframe>`.

### `pedir-turno`
- Formulario con `[(ngModel)]` para two-way binding (módulo `FormsModule`).
- `formatearRut()` aplica el formato `12.345.678-9` en tiempo real al tipear.
- `fechaMin` se calcula en `ngOnInit()` para bloquear fechas pasadas.

---

## Decisiones de diseño

- **Mismo diseño visual** que el HTML original: mismos colores, tipografías, layouts y animaciones.
- **Componentes standalone** (sin NgModules) — patrón moderno de Angular 17.
- **CSS por componente**: cada página tiene su propio CSS con alcance local, eliminando los conflictos de los archivos globales del proyecto original.
- **Sin librerías externas de UI**: el diseño se mantiene 100% con CSS propio.
- **Font Awesome** se carga desde CDN en `index.html` (igual que en el original).
