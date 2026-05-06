import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Usuario } from '../../features/auth/models/usuario.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly USER_KEY = 'clinica_current_user';
  private readonly URL = `${environment.apiUrl}/api/v1/usuarios`;

  constructor(private http: HttpClient) {}

  // TEMPORAL: usa GET /api/v1/usuarios hasta que Ignacio implemente POST /api/v1/auth/login
  login(username: string, password: string): Observable<Usuario> {
    return this.http.get<Usuario[]>(this.URL).pipe(
      map(usuarios => {
        const user = usuarios.find(u => u.username === username && u.password === password);
        if (!user) throw new Error('Credenciales incorrectas');
        this.setCurrentUser(user);
        return user;
      })
    );
  }

  register(userData: Omit<Usuario, 'id'>): Observable<Usuario> {
    return this.http.post<Usuario>(this.URL, userData).pipe(
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
    return this.getCurrentUser()?.rol === 'admin';
  }

  logout(): void {
    localStorage.removeItem(this.USER_KEY);
  }
}
