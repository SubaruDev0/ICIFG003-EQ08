import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Servicio } from '../models/servicio.model';

@Injectable({ providedIn: 'root' })
export class ServicioService {
  private url = `${environment.apiUrl}/api/v1/servicios`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.url);
  }

  findById(id: number): Observable<Servicio> {
    return this.http.get<Servicio>(`${this.url}/${id}`);
  }

  save(servicio: Servicio): Observable<Servicio> {
    return this.http.post<Servicio>(this.url, servicio);
  }

  update(id: number, servicio: Servicio): Observable<Servicio> {
    return this.http.put<Servicio>(`${this.url}/${id}`, servicio);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
