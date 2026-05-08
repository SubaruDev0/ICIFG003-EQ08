import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Turno } from '../models/turno.model';

@Injectable({ providedIn: 'root' })
export class TurnoService {
  private url = `${environment.apiUrl}/api/v1/turnos`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Turno[]> {
    return this.http.get<Turno[]>(this.url);
  }

  findById(id: number): Observable<Turno> {
    return this.http.get<Turno>(`${this.url}/${id}`);
  }

  save(turno: Turno): Observable<Turno> {
    return this.http.post<Turno>(this.url, turno);
  }

  horariosDisponibles(servicioId: number, fecha: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.url}/disponibles?servicioId=${servicioId}&fecha=${fecha}`);
  }

  actualizarEstado(id: number, estado: 'CONFIRMADO' | 'CANCELADO' | 'COMPLETADO'): Observable<any> {
    return this.http.patch(`${this.url}/${id}/estado?estado=${estado}`, {});
  }

  update(id: number, turno: Turno): Observable<Turno> {
    return this.http.put<Turno>(`${this.url}/${id}`, turno);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
