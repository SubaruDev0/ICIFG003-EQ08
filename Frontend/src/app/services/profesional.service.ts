import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Profesional } from '../models/profesional.model';

@Injectable({ providedIn: 'root' })
export class ProfesionalService {
  private url = `${environment.apiUrl}/api/v1/profesionales`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Profesional[]> {
    return this.http.get<Profesional[]>(this.url);
  }

  findById(id: number): Observable<Profesional> {
    return this.http.get<Profesional>(`${this.url}/${id}`);
  }

  save(profesional: Profesional): Observable<Profesional> {
    return this.http.post<Profesional>(this.url, profesional);
  }

  update(id: number, profesional: Profesional): Observable<Profesional> {
    return this.http.put<Profesional>(`${this.url}/${id}`, profesional);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
