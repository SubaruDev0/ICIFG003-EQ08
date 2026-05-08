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
  horariosDisponibles: string[] = [];
  mensaje = '';
  terminos = false;
  fechaMin = '';

  enviando = false;
  exito = false;
  errorEnvio = '';

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

  onSubmit(): void {
    if (!this.servicioSeleccionado) {
      this.errorEnvio = 'Seleccione un servicio.';
      return;
    }

    if (!this.terminos) {
      this.errorEnvio = 'Debe aceptar los términos y condiciones.';
      return;
    }

    this.enviando = true;
    this.errorEnvio = '';

    const paciente = {
      nombreCompleto: this.nombre,
      rut: this.rut,
      telefono: this.telefono,
      email: this.email
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
            this.exito = true;
            this.resetForm();
          },
          error: () => {
            this.enviando = false;
            this.errorEnvio = 'Error al registrar el turno. El horario podría no estar disponible.';
          }
        });
      },
      error: () => {
        this.enviando = false;
        this.errorEnvio = 'Error al registrar el paciente. Verifique los datos ingresados.';
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
