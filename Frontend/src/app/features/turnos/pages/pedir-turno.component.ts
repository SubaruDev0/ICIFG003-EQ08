import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header.component';
import { FooterComponent } from '../../../shared/components/footer.component';
import { ServicioService } from '../../servicios/services/servicio.service';
import { PacienteService } from '../../pacientes/services/paciente.service';
import { TurnoService } from '../services/turno.service';
import { Servicio } from '../../servicios/models/servicio.model';
import Swal from 'sweetalert2';

const TODOS_LOS_HORARIOS: string[] = [];
for (let h = 9; h <= 18; h++) {
  TODOS_LOS_HORARIOS.push(`${String(h).padStart(2, '0')}:00`);
  if (h < 18) TODOS_LOS_HORARIOS.push(`${String(h).padStart(2, '0')}:30`);
}

@Component({
  selector: 'app-pedir-turno',
  standalone: true,
  imports: [FormsModule, CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './pedir-turno.component.html',
  styleUrl: './pedir-turno.component.css'
})
export class PedirTurnoComponent implements OnInit {
  nombre = '';
  rut = '';
  telefono = '';
  email = '';
  servicioSeleccionado: Servicio | null = null;
  servicios: Servicio[] = [];
  fecha = '';
  horario = '';
  todosLosHorarios = TODOS_LOS_HORARIOS;
  horariosDisponibles: string[] = [];
  mensaje = '';
  terminos = false;
  fechaMin = '';
  enviando = false;

  constructor(
    private route: ActivatedRoute,
    private servicioService: ServicioService,
    private pacienteService: PacienteService,
    private turnoService: TurnoService
  ) {}

  ngOnInit(): void {
    this.fechaMin = new Date().toISOString().split('T')[0];
    const servicioIdParam = this.route.snapshot.queryParamMap.get('servicioId');

    this.servicioService.findAll().subscribe({
      next: (data) => {
        this.servicios = data;
        if (servicioIdParam) {
          const id = Number(servicioIdParam);
          this.servicioSeleccionado = this.servicios.find(s => s.id === id) || null;
          this.cargarHorarios();
        }
      },
      error: () => { this.servicios = []; }
    });
  }

  onServicioChange(): void {
    this.horario = '';
    this.cargarHorarios();
  }

  onFechaChange(): void {
    this.horario = '';
    this.cargarHorarios();
  }

  estaDisponible(h: string): boolean {
    return this.horariosDisponibles.includes(h);
  }

  private cargarHorarios(): void {
    this.horariosDisponibles = [];
    if (!this.servicioSeleccionado?.id || !this.fecha) return;

    this.turnoService.horariosDisponibles(this.servicioSeleccionado.id, this.fecha).subscribe({
      next: (horarios) => { this.horariosDisponibles = horarios; },
      error: () => { this.horariosDisponibles = []; }
    });
  }

  formatearRut(): void {
    let value = this.rut.replace(/\D/g, '');
    if (value.length > 1) {
      const cuerpo = value.slice(0, -1).replace(/\B(?=(\d{3})+(?!\d))/g, '.');
      const dv = value.slice(-1);
      this.rut = `${cuerpo}-${dv}`;
    } else {
      this.rut = value;
    }
  }

  private rutParaApi(): string {
    return this.rut.replace(/\./g, '');
  }

  onSubmit(): void {
    if (!this.nombre.trim() || !this.rut.trim() || !this.telefono.trim() || !this.email.trim()) {
      Swal.fire({ icon: 'warning', title: 'Campos incompletos', text: 'Por favor completa todos los campos obligatorios.', confirmButtonColor: '#0fa49c' });
      return;
    }
    if (!this.servicioSeleccionado) {
      Swal.fire({ icon: 'warning', title: 'Falta el servicio', text: 'Selecciona el servicio que necesitas.', confirmButtonColor: '#0fa49c' });
      return;
    }
    if (!this.fecha) {
      Swal.fire({ icon: 'warning', title: 'Falta la fecha', text: 'Selecciona una fecha para el turno.', confirmButtonColor: '#0fa49c' });
      return;
    }
    if (!this.horario) {
      Swal.fire({ icon: 'warning', title: 'Falta el horario', text: 'Selecciona un horario disponible.', confirmButtonColor: '#0fa49c' });
      return;
    }
    if (!this.terminos) {
      Swal.fire({ icon: 'warning', title: 'Términos y condiciones', text: 'Debes aceptar los términos para continuar.', confirmButtonColor: '#0fa49c' });
      return;
    }

    this.enviando = true;

    const paciente = {
      nombreCompleto: this.nombre.trim(),
      rut: this.rutParaApi(),
      telefono: this.telefono.trim(),
      email: this.email.trim()
    };

    this.pacienteService.save(paciente).subscribe({
      next: (pacienteCreado) => {
        const turno: any = {
          fecha: this.fecha,
          horario: this.horario,
          mensajeAdicional: this.mensaje,
          paciente: { id: pacienteCreado.id },
          servicio: { id: this.servicioSeleccionado!.id }
        };

        this.turnoService.save(turno).subscribe({
          next: () => {
            this.enviando = false;
            this.resetForm();
            Swal.fire({
              icon: 'success',
              title: '¡Turno solicitado!',
              html: 'Tu turno fue registrado correctamente.<br>Te contactaremos para confirmar.',
              confirmButtonColor: '#0fa49c',
              timer: 3000,
              showConfirmButton: false
            });
          },
          error: () => {
            this.enviando = false;
            Swal.fire({ icon: 'error', title: 'Horario no disponible', text: 'Ese horario ya fue reservado. Por favor elige otro horario o fecha.', confirmButtonColor: '#0fa49c' });
          }
        });
      },
      error: () => {
        this.enviando = false;
        Swal.fire({ icon: 'error', title: 'Error al guardar', text: 'No se pudo registrar el paciente. Verifica que el RUT y el email sean válidos.', confirmButtonColor: '#0fa49c' });
      }
    });
  }

  private resetForm(): void {
    this.nombre = '';
    this.rut = '';
    this.telefono = '';
    this.email = '';
    this.servicioSeleccionado = null;
    this.fecha = '';
    this.horario = '';
    this.horariosDisponibles = [];
    this.mensaje = '';
    this.terminos = false;
  }
}
