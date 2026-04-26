import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';

@Component({
  selector: 'app-pedir-turno',
  standalone: true,
  imports: [FormsModule, HeaderComponent, FooterComponent],
  templateUrl: './pedir-turno.component.html',
  styleUrl: './pedir-turno.component.css'
})
export class PedirTurnoComponent implements OnInit {
  nombre = '';
  rut = '';
  telefono = '';
  email = '';
  servicio = '';
  fecha = '';
  horario = '';
  mensaje = '';
  terminos = false;
  fechaMin = '';

  ngOnInit(): void {
    this.fechaMin = new Date().toISOString().split('T')[0];
  }

  formatearRut(): void {
    let value = this.rut.replace(/\D/g, '');
    if (value.length > 1) {
      const cuerpo = value.slice(0, -1).replace(/\B(?=(\d{3})+(?!\d))/g, '.');
      const dv = value.slice(-1);
      this.rut = `${cuerpo}-${dv}`;
    } else {
      this.rut = value;
    }
  }

  onSubmit(): void {
    alert('Turno solicitado correctamente. Nos contactaremos para confirmar.');
  }
}
