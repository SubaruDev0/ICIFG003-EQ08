import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header.component';
import { FooterComponent } from '../../../shared/components/footer.component';
import { TurnoService } from '../services/turno.service';
import { AuthService } from '../../../core/services/auth.service';
import { Turno } from '../models/turno.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestionar-turnos',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './gestionar-turnos.component.html',
  styleUrl: './gestionar-turnos.component.css'
})
export class GestionarTurnosComponent implements OnInit {
  turnos: Turno[] = [];
  cargando = true;
  filtroEstado: string = 'TODOS';

  constructor(
    private turnoService: TurnoService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isProfesionalOAdmin()) {
      this.router.navigate(['/inicio']);
      return;
    }
    this.cargarTurnos();
  }

  cargarTurnos(): void {
    this.cargando = true;
    this.turnoService.findAll().subscribe({
      next: (data) => {
        this.turnos = data.sort((a: any, b: any) =>
          new Date(a.fecha).getTime() - new Date(b.fecha).getTime()
        );
        this.cargando = false;
      },
      error: () => { this.cargando = false; }
    });
  }

  get turnosFiltrados(): Turno[] {
    if (this.filtroEstado === 'TODOS') return this.turnos;
    return this.turnos.filter((t: any) => t.estado === this.filtroEstado);
  }

  confirmar(turno: any): void {
    Swal.fire({
      title: '¿Confirmar cita?',
      text: `${turno.paciente?.nombreCompleto} — ${turno.horario} del ${this.formatFecha(turno.fecha)}`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, confirmar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#0fa49c'
    }).then(result => {
      if (result.isConfirmed) {
        this.turnoService.actualizarEstado(turno.id, 'CONFIRMADO').subscribe({
          next: () => {
            turno.estado = 'CONFIRMADO';
            Swal.fire({ icon: 'success', title: 'Cita confirmada', timer: 1500, showConfirmButton: false });
          },
          error: () => Swal.fire({ icon: 'error', title: 'Error al confirmar', confirmButtonColor: '#0fa49c' })
        });
      }
    });
  }

  rechazar(turno: any): void {
    Swal.fire({
      title: '¿Rechazar cita?',
      text: `Se cancelará el turno de ${turno.paciente?.nombreCompleto}.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, rechazar',
      cancelButtonText: 'Volver',
      confirmButtonColor: '#e74c3c'
    }).then(result => {
      if (result.isConfirmed) {
        this.turnoService.actualizarEstado(turno.id, 'CANCELADO').subscribe({
          next: () => {
            turno.estado = 'CANCELADO';
            Swal.fire({ icon: 'info', title: 'Cita rechazada', timer: 1500, showConfirmButton: false });
          },
          error: () => Swal.fire({ icon: 'error', title: 'Error al rechazar', confirmButtonColor: '#0fa49c' })
        });
      }
    });
  }

  completar(turno: any): void {
    this.turnoService.actualizarEstado(turno.id, 'COMPLETADO').subscribe({
      next: () => { turno.estado = 'COMPLETADO'; },
      error: () => Swal.fire({ icon: 'error', title: 'Error', confirmButtonColor: '#0fa49c' })
    });
  }

  formatFecha(fecha: string): string {
    if (!fecha) return '';
    const d = new Date(fecha + 'T00:00:00');
    return d.toLocaleDateString('es-CL', { day: '2-digit', month: 'long', year: 'numeric' });
  }

  badgeClase(estado: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'badge-pendiente',
      CONFIRMADO: 'badge-confirmado',
      CANCELADO: 'badge-cancelado',
      COMPLETADO: 'badge-completado'
    };
    return map[estado] ?? '';
  }
}
