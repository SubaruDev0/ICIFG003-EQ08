import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../shared/components/header.component';
import { FooterComponent } from '../../../shared/components/footer.component';
import { ProfesionalService } from '../services/profesional.service';
import { Profesional } from '../models/profesional.model';

@Component({
  selector: 'app-equipo',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, CommonModule],
  templateUrl: './equipo.component.html',
  styleUrl: './equipo.component.css'
})
export class EquipoComponent implements OnInit {
  profesionales: Profesional[] = [];
  cargando = true;
  errorCarga = false;

  constructor(private router: Router, private profesionalService: ProfesionalService) {}

  ngOnInit(): void {
    this.profesionalService.findAll().subscribe({
      next: (data) => {
        this.profesionales = data;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        this.errorCarga = true;
      }
    });
  }

  irAPedirTurno(): void {
    this.router.navigate(['/pedir-turno']);
  }
}
