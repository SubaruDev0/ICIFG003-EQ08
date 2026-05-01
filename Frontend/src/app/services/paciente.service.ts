import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Paciente } from '../models/paciente.model';

@Injectable({ providedIn: 'root' })
export class PacienteService {
  private url = `${environment.apiUrl}/api/v1/pacientes`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.url);
  }

  findById(id: number): Observable<Paciente> {
    return this.http.get<Paciente>(`${this.url}/${id}`);
  }

  save(paciente: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.url, paciente);
  }

  update(id: number, paciente: Paciente): Observable<Paciente> {
    return this.http.put<Paciente>(`${this.url}/${id}`, paciente);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
