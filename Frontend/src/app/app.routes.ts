import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login.component';
import { RegisterComponent } from './features/auth/pages/register.component';
import { InicioComponent } from './features/inicio/pages/inicio.component';
import { ServiciosComponent } from './features/servicios/pages/servicios.component';
import { CrearServicioComponent } from './features/servicios/pages/crear-servicio.component';
import { EquipoComponent } from './features/profesionales/pages/equipo.component';
import { ContactoComponent } from './features/contacto/pages/contacto.component';
import { PedirTurnoComponent } from './features/turnos/pages/pedir-turno.component';
import { GestionarTurnosComponent } from './features/turnos/pages/gestionar-turnos.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'inicio', component: InicioComponent },
  { path: 'servicios', component: ServiciosComponent },
  { path: 'admin/servicios/nuevo', component: CrearServicioComponent },
  { path: 'equipo', component: EquipoComponent },
  { path: 'contacto', component: ContactoComponent },
  { path: 'pedir-turno', component: PedirTurnoComponent },
  { path: 'gestionar-turnos', component: GestionarTurnosComponent },
  { path: '**', redirectTo: 'login' }
];
