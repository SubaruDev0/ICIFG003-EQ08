import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login.component';
import { InicioComponent } from './features/inicio/pages/inicio.component';
import { ServiciosComponent } from './features/servicios/pages/servicios.component';
import { EquipoComponent } from './features/profesionales/pages/equipo.component';
import { ContactoComponent } from './features/contacto/pages/contacto.component';
import { PedirTurnoComponent } from './features/turnos/pages/pedir-turno.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'inicio', component: InicioComponent },
  { path: 'servicios', component: ServiciosComponent },
  { path: 'equipo', component: EquipoComponent },
  { path: 'contacto', component: ContactoComponent },
  { path: 'pedir-turno', component: PedirTurnoComponent },
];
