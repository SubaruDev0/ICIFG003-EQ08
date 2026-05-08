import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../shared/components/header.component';
import { FooterComponent } from '../../../shared/components/footer.component';
import { ServicioService } from '../services/servicio.service';
import { AuthService } from '../../../core/services/auth.service';
import Swal from 'sweetalert2';
import { animate } from 'animejs';

@Component({
  selector: 'app-crear-servicio',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, FormsModule, CommonModule, RouterLink],
  templateUrl: './crear-servicio.component.html',
  styleUrl: './crear-servicio.component.css'
})
export class CrearServicioComponent implements OnInit {
  nombre = '';
  imagenBase64 = '';
  imagenPreview = '';
  guardando = false;

  constructor(
    private router: Router,
    private servicioService: ServicioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isProfesionalOAdmin()) {
      this.router.navigate(['/inicio']);
      return;
    }
    setTimeout(() => {
      animate('.form-card', { opacity: [0, 1], translateY: [30, 0], duration: 600, ease: 'outCubic' });
    }, 50);
  }

  onImagenChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      this.imagenPreview = result;
      this.imagenBase64 = result.split(',')[1];
    };
    reader.readAsDataURL(file);
  }

  onSubmit(): void {
    if (!this.nombre.trim()) {
      Swal.fire({ icon: 'warning', title: 'Campo requerido', text: 'Ingresa el nombre del servicio.', confirmButtonColor: '#0fa49c' });
      return;
    }
    this.guardando = true;
    this.servicioService.save({ nombre: this.nombre.trim(), imagenBase64: this.imagenBase64 || undefined }).subscribe({
      next: () => {
        this.guardando = false;
        Swal.fire({
          icon: 'success',
          title: '¡Servicio creado!',
          text: `"${this.nombre}" fue agregado exitosamente.`,
          confirmButtonColor: '#0fa49c',
          showDenyButton: true,
          denyButtonText: 'Ir a Servicios',
          confirmButtonText: 'Crear otro'
        }).then(result => {
          if (result.isDenied) {
            this.router.navigate(['/servicios']);
          } else {
            this.nombre = '';
            this.imagenBase64 = '';
            this.imagenPreview = '';
          }
        });
      },
      error: () => {
        this.guardando = false;
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo crear el servicio.', confirmButtonColor: '#0fa49c' });
      }
    });
  }
}
