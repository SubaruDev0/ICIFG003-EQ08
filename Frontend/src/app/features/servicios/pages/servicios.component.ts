import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../shared/components/header.component';
import { FooterComponent } from '../../../shared/components/footer.component';
import { ServicioService } from '../services/servicio.service';
import { AuthService } from '../../../core/services/auth.service';
import { Servicio } from '../models/servicio.model';
import { animate, stagger } from 'animejs';

@Component({
  selector: 'app-servicios',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, CommonModule],
  templateUrl: './servicios.component.html',
  styleUrl: './servicios.component.css'
})
export class ServiciosComponent implements OnInit {
  servicios: Servicio[] = [];
  cargando = true;
  error = '';

  constructor(
    private router: Router,
    private servicioService: ServicioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.servicioService.findAll().subscribe({
      next: (data) => {
        this.servicios = data;
        this.cargando = false;
        setTimeout(() => {
          animate('.servicio-card', { opacity: [0, 1], translateY: [20, 0], delay: stagger(80), duration: 500, ease: 'outCubic' });
        }, 50);
      },
      error: () => {
        this.cargando = false;
        this.error = 'No se pudieron cargar los servicios. Verifica que el servidor esté activo.';
      }
    });
  }

  get isAdmin(): boolean { return this.authService.isAdmin(); }
  irAPedirTurno(): void { this.router.navigate(['/pedir-turno']); }
  irACrearServicio(): void { this.router.navigate(['/admin/servicios/nuevo']); }
}
