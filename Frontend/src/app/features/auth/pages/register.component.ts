import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { ProfesionalService } from '../../profesionales/services/profesional.service';
import { ServicioService } from '../../servicios/services/servicio.service';
import { Servicio } from '../../servicios/models/servicio.model';
import Swal from 'sweetalert2';
import { animate, stagger } from 'animejs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  username = '';
  password = '';
  rol: 'paciente' | 'admin' = 'paciente';
  imagenBase64 = '';
  fotoPreview = '';
  servicioSeleccionado: Servicio | null = null;
  servicios: Servicio[] = [];
  registrando = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private profesionalService: ProfesionalService,
    private servicioService: ServicioService
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/inicio']);
      return;
    }
    this.servicioService.findAll().subscribe({
      next: (data) => { this.servicios = data; },
      error: () => { this.servicios = []; }
    });
    setTimeout(() => this.animarEntrada(), 100);
  }

  private animarEntrada(): void {
    animate('.tarjeta-registro', { opacity: [0, 1], translateY: [30, 0], duration: 700, ease: 'outCubic' });
  }

  onFotoChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      this.fotoPreview = result;
      this.imagenBase64 = result.split(',')[1];
    };
    reader.readAsDataURL(file);
  }

  seleccionarRol(rol: 'paciente' | 'admin'): void {
    this.rol = rol;
    this.servicioSeleccionado = null;
  }

  onSubmit(): void {
    if (!this.username.trim() || !this.password.trim()) {
      Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Completa usuario y contraseña.', confirmButtonColor: '#0fa49c' });
      return;
    }
    if (this.rol === 'admin' && !this.servicioSeleccionado) {
      Swal.fire({ icon: 'warning', title: 'Selecciona especialidad', text: 'Los profesionales deben seleccionar su especialidad.', confirmButtonColor: '#0fa49c' });
      return;
    }

    this.registrando = true;
    const userData = { username: this.username, password: this.password, rol: this.rol, imagenBase64: this.imagenBase64 };

    this.authService.register(userData).subscribe({
      next: (usuario) => {
        if (this.rol === 'admin') {
          this.profesionalService.save({
            nombreCompleto: usuario.username,
            imagenBase64: this.imagenBase64,
            servicio: this.servicioSeleccionado!
          }).subscribe({
            next: () => this.finalizarRegistro(),
            error: () => this.finalizarRegistro()
          });
        } else {
          this.finalizarRegistro();
        }
      },
      error: () => {
        this.registrando = false;
        Swal.fire({ icon: 'error', title: 'Error al registrar', text: 'No se pudo crear la cuenta. Intenta nuevamente.', confirmButtonColor: '#0fa49c' });
      }
    });
  }

  compararServicio(a: Servicio | null, b: Servicio | null): boolean {
    return a?.id === b?.id;
  }

  private finalizarRegistro(): void {
    this.registrando = false;
    Swal.fire({
      icon: 'success',
      title: '¡Cuenta creada!',
      text: `Bienvenido/a, ${this.username}`,
      confirmButtonColor: '#0fa49c',
      timer: 2000,
      showConfirmButton: false
    }).then(() => this.router.navigate(['/inicio']));
  }
}
