import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header.component';
import { FooterComponent } from '../../../shared/components/footer.component';

@Component({
  selector: 'app-servicios',
  standalone: true,
  imports: [HeaderComponent, FooterComponent],
  templateUrl: './servicios.component.html',
  styleUrl: './servicios.component.css'
})
export class ServiciosComponent {
  constructor(private router: Router) {}

  irAPedirTurno(): void {
    this.router.navigate(['/pedir-turno']);
  }
}
