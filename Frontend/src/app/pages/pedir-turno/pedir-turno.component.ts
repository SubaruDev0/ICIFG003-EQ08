import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';
import { ServicioService } from '../../services/servicio.service';
import { PacienteService } from '../../services/paciente.service';
import { TurnoService } from '../../services/turno.service';
import { Servicio } from '../../models/servicio.model';

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
  mensaje = '';
  terminos = false;
  fechaMin = '';

  enviando = false;
  exito = false;
  errorEnvio = '';

  constructor(
    private servicioService: ServicioService,
    private pacienteService: PacienteService,
    private turnoService: TurnoService
  ) {}

  ngOnInit(): void {
    this.fechaMin = new Date().toISOString().split('T')[0];
    this.servicioService.findAll().subscribe({
      next: (data) => { this.servicios = data; },
      error: () => { this.servicios = []; }
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
        const turno = {
          fecha: this.fecha,
          horario: this.horario,
          mensajeAdicional: this.mensaje,
          paciente: pacienteCreado,
          servicio: this.servicioSeleccionado!
        };

        this.turnoService.save(turno).subscribe({
          next: () => {
            this.enviando = false;
            this.exito = true;
            this.resetForm();
          },
          error: () => {
            this.enviando = false;
            this.errorEnvio = 'Error al registrar el turno. Intente nuevamente.';
          }
        });
      },
      error: () => {
        this.enviando = false;
        this.errorEnvio = 'Error al registrar el paciente. Intente nuevamente.';
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
    this.mensaje = '';
    this.terminos = false;
  }
}
