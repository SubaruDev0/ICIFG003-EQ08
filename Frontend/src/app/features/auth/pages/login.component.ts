import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  usuario = '';
  contrasena = '';
  pantallaVisible = true;
  loginVisible = false;
  cargando = false;

  private T_EXPANSION = 800;
  private T_ROTACION_ENTRADA = 700;
  private T_ESPERA_LOGO = 800;
  private T_ROTACION_SALIDA = 600;

  logoEstado: 'oculto' | 'mostrar' | 'desvanecer' | 'final' = 'oculto';

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/inicio']);
      return;
    }
    this.iniciarSecuencia();
  }

  private esperar(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private async iniciarSecuencia(): Promise<void> {
    await this.esperar(this.T_EXPANSION);
    this.logoEstado = 'mostrar';
    await this.esperar(this.T_ROTACION_ENTRADA + this.T_ESPERA_LOGO);
    this.logoEstado = 'desvanecer';
    await this.esperar(this.T_ROTACION_SALIDA);
    this.logoEstado = 'final';
    this.pantallaVisible = false;
    this.loginVisible = true;
  }

  onSubmit(): void {
    if (!this.usuario || !this.contrasena) {
      Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Completa usuario y contraseña.', confirmButtonColor: '#0fa49c' });
      return;
    }
    this.cargando = true;
    this.authService.login(this.usuario, this.contrasena).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/inicio']);
      },
      error: () => {
        this.cargando = false;
        Swal.fire({ icon: 'error', title: 'Acceso denegado', text: 'Usuario o contraseña incorrectos.', confirmButtonColor: '#0fa49c' });
      }
    });
  }

  accederInvitado(): void {
    this.router.navigate(['/inicio']);
  }
}
