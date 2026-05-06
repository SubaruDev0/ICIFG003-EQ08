import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../services/usuario.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  usuario = '';
  contrasena = '';
  mostrarError = false;
  textoError = '';
  pantallaVisible = true;
  loginVisible = false;
  cargando = false;

  private T_EXPANSION = 800;
  private T_ROTACION_ENTRADA = 700;
  private T_ESPERA_LOGO = 800;
  private T_ROTACION_SALIDA = 600;
  private T_DURACION_ERROR = 4000;

  logoEstado: 'oculto' | 'mostrar' | 'desvanecer' | 'final' = 'oculto';

  constructor(private router: Router, private usuarioService: UsuarioService) {}

  ngOnInit(): void {
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
    this.cargando = true;
    this.mostrarError = false;

    this.usuarioService.findAll().subscribe({
      next: (usuarios) => {
        const encontrado = usuarios.find(
          u => u.username === this.usuario && u.password === this.contrasena
        );
        this.cargando = false;
        if (encontrado) {
          this.router.navigate(['/inicio']);
        } else {
          this.mostrarErrorTemporal('Usuario o contraseña incorrectos.');
        }
      },
      error: () => {
        this.cargando = false;
        this.mostrarErrorTemporal('No se pudo conectar al servidor. Intente nuevamente.');
      }
    });
  }

  accederInvitado(): void {
    this.mostrarError = false;
    this.router.navigate(['/inicio']);
  }

  private mostrarErrorTemporal(mensaje: string): void {
    this.textoError = mensaje;
    this.mostrarError = true;
    setTimeout(() => { this.mostrarError = false; }, this.T_DURACION_ERROR);
  }
}
