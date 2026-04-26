import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';

@Component({
  selector: 'app-equipo',
  standalone: true,
  imports: [HeaderComponent, FooterComponent],
  templateUrl: './equipo.component.html',
  styleUrl: './equipo.component.css'
})
export class EquipoComponent {
  constructor(private router: Router) {}

  irAPedirTurno(): void {
    this.router.navigate(['/pedir-turno']);
  }
}
