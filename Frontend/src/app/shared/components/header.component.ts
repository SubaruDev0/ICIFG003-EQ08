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

  get avatarSrc(): string {
    if (this.currentUser?.imagenBase64) {
      return 'data:image/jpeg;base64,' + this.currentUser.imagenBase64;
    }
    return 'assets/img/logo.png';
  }

  get rolLabel(): string {
    return this.currentUser?.rol === 'ADMIN' ? 'Administrador' : 'Paciente';
  }

  logout(): void {
    this.authService.logout();
    this.currentUser = null;
    this.router.navigate(['/login']);
  }
}
