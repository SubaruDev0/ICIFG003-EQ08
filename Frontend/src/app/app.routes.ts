import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./features/auth/pages/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/pages/register.component').then(m => m.RegisterComponent) },
  { path: 'inicio', loadComponent: () => import('./features/inicio/pages/inicio.component').then(m => m.InicioComponent) },
  { path: 'servicios', loadComponent: () => import('./features/servicios/pages/servicios.component').then(m => m.ServiciosComponent) },
  { path: 'admin/servicios/nuevo', loadComponent: () => import('./features/servicios/pages/crear-servicio.component').then(m => m.CrearServicioComponent) },
  { path: 'equipo', loadComponent: () => import('./features/profesionales/pages/equipo.component').then(m => m.EquipoComponent) },
  { path: 'contacto', loadComponent: () => import('./features/contacto/pages/contacto.component').then(m => m.ContactoComponent) },
  { path: 'pedir-turno', loadComponent: () => import('./features/turnos/pages/pedir-turno.component').then(m => m.PedirTurnoComponent) },
  { path: 'gestionar-turnos', loadComponent: () => import('./features/turnos/pages/gestionar-turnos.component').then(m => m.GestionarTurnosComponent) },
  { path: '**', redirectTo: 'login' }
];
