import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Usuario } from '../../features/auth/models/usuario.model';
import { environment } from '../../../environments/environment';

interface LoginPayload {
  username: string;
  password: string;
}

interface RegisterPayload {
  username: string;
  password: string;
  rol: 'admin' | 'paciente';
  imagenBase64?: string;
  servicioId?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly USER_KEY = 'clinica_current_user';
  private readonly AUTH_URL = `${environment.apiUrl}/api/v1/auth`;

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<Usuario> {
    const payload: LoginPayload = { username, password };
    return this.http.post<Usuario>(`${this.AUTH_URL}/login`, payload).pipe(
      map(user => {
        this.setCurrentUser(user);
        return user;
      })
    );
  }

  register(userData: RegisterPayload): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.AUTH_URL}/register`, userData).pipe(
      map(user => {
        this.setCurrentUser(user);
        return user;
      })
    );
  }

  getCurrentUser(): Usuario | null {
    const data = localStorage.getItem(this.USER_KEY);
    return data ? JSON.parse(data) : null;
  }

  setCurrentUser(user: Usuario): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  isLoggedIn(): boolean {
    return !!this.getCurrentUser();
  }

  isAdmin(): boolean {
    return this.getCurrentUser()?.rol === 'ADMIN';
  }

  isProfesionalOAdmin(): boolean {
    const rol = this.getCurrentUser()?.rol;
    return rol === 'ADMIN' || rol === 'PROFESIONAL';
  }

  logout(): void {
    localStorage.removeItem(this.USER_KEY);
  }
}
