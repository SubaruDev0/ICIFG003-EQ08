import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { Usuario } from '../../features/auth/models/usuario.model';
import { animate, stagger } from 'animejs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  currentUser: Usuario | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      setTimeout(() => {
        animate('.user-section', { opacity: [0, 1], translateX: [20, 0], duration: 500, ease: 'outCubic' });
      }, 100);
    }
  }

  get isLoggedIn(): boolean { return !!this.currentUser; }
  get isAdmin(): boolean { return this.currentUser?.rol === 'ADMIN'; }
  get isProfesionalOAdmin(): boolean {
    const rol = this.currentUser?.rol;
    return rol === 'ADMIN' || rol === 'PROFESIONAL';
  }

  get avatarSrc(): string {
    const img = this.currentUser?.imagenBase64;
    if (!img) return 'assets/img/logo.png';
    return img.startsWith('http') ? img : 'data:image/jpeg;base64,' + img;
  }

  get rolLabel(): string {
    const map: Record<string, string> = {
      ADMIN: 'Administrador',
      PROFESIONAL: 'Profesional',
      PACIENTE: 'Paciente'
    };
    return map[this.currentUser?.rol ?? ''] ?? 'Usuario';
  }

  logout(): void {
    this.authService.logout();
    this.currentUser = null;
    this.router.navigate(['/login']);
  }
}
